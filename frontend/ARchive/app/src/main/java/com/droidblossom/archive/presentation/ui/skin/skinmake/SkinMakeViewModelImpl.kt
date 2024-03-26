package com.droidblossom.archive.presentation.ui.skin.skinmake

import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.droidblossom.archive.domain.model.capsule_skin.CapsuleSkinsMakeRequest
import com.droidblossom.archive.domain.model.capsule_skin.SkinMotion
import com.droidblossom.archive.domain.model.s3.S3UrlRequest
import com.droidblossom.archive.domain.usecase.capsule_skin.CapsuleSkinsMakeUseCase
import com.droidblossom.archive.domain.usecase.s3.S3UrlsGetUseCase
import com.droidblossom.archive.presentation.base.BaseViewModel
import com.droidblossom.archive.presentation.ui.auth.AuthViewModel
import com.droidblossom.archive.presentation.ui.home.createcapsule.CreateCapsuleViewModel
import com.droidblossom.archive.presentation.ui.home.createcapsule.CreateCapsuleViewModelImpl
import com.droidblossom.archive.util.Motion
import com.droidblossom.archive.util.Retarget
import com.droidblossom.archive.util.S3Util
import com.droidblossom.archive.util.onFail
import com.droidblossom.archive.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SkinMakeViewModelImpl @Inject constructor(
    private val s3UrlsGetUseCase: S3UrlsGetUseCase,
    private val capsuleSkinsMakeUseCase: CapsuleSkinsMakeUseCase,
    private val s3Util: S3Util
) : BaseViewModel(), SkinMakeViewModel {

    companion object {
        const val S3DIRECTORY = "capsuleSkin"
        const val IMAGEEXTENSION = "image/jpeg"
    }

    private val _skinMakeEvents = MutableSharedFlow<SkinMakeViewModel.SkinMakeEvent>()
    override val skinMakeEvents: SharedFlow<SkinMakeViewModel.SkinMakeEvent> =
        _skinMakeEvents.asSharedFlow()

    override val skinImgUri = MutableStateFlow<Uri?>(null)

    private val _addMotion = MutableStateFlow(false)
    override val addMotion: StateFlow<Boolean> get() = _addMotion

    private val _skinImgFile = MutableStateFlow<File?>(null)
    override val skinImgFile: StateFlow<File?> = _skinImgFile

    private val _skinMotions = MutableStateFlow<List<SkinMotion>>(
        listOf(
            SkinMotion(
                1L,
                "https://cdn.pixabay.com/animation/2022/10/11/09/05/09-05-26-529_512.gif",
                Motion.JUMPING_JACKS,
                Retarget.CMU,
                false
            ),
            SkinMotion(
                2L,
                "https://cdn.pixabay.com/animation/2022/10/11/09/05/09-05-26-529_512.gif",
                Motion.DAB,
                Retarget.FAIR,
                false
            ),
            SkinMotion(
                3L,
                "https://cdn.pixabay.com/animation/2022/10/11/09/05/09-05-26-529_512.gif",
                Motion.JUMPING,
                Retarget.FAIR,
                false
            ),
            SkinMotion(
                4L,
                "https://cdn.pixabay.com/animation/2022/10/11/09/05/09-05-26-529_512.gif",
                Motion.WAVE_HELLO,
                Retarget.FAIR,
                false
            ),
            SkinMotion(
                5L,
                "https://cdn.pixabay.com/animation/2022/10/11/09/05/09-05-26-529_512.gif",
                Motion.ZOMBIE,
                Retarget.FAIR,
                false
            ),
            SkinMotion(
                6L,
                "https://cdn.pixabay.com/animation/2022/10/11/09/05/09-05-26-529_512.gif",
                Motion.JESSE_DANCE,
                Retarget.ROKOKO,
                false
            )
        )
    )
    override val skinMotions get() = _skinMotions

    private val _skinMotionIndex = MutableStateFlow<Int>(-1)
    override val skinMotionIndex: StateFlow<Int>
        get() = _skinMotionIndex
    override fun selectSkinMotion(skinMotion: SkinMotion) {
        viewModelScope.launch {
            val newList = skinMotions.value.map { existingSkinMotion ->
                if (existingSkinMotion == skinMotion) {
                    existingSkinMotion.copy(isClicked = true)
                } else {
                    existingSkinMotion.copy(isClicked = false)
                }
            }
            _skinMotions.emit(newList)
        }
    }
    override fun skinMakeEvent(event: SkinMakeViewModel.SkinMakeEvent) {
        viewModelScope.launch {
            _skinMakeEvents.emit(event)
        }
    }

    override val skinName = MutableStateFlow("")

    override fun selectAddMotion() {
        viewModelScope.launch {
            if (addMotion.value) {
                val newList = skinMotions.value.map { skinMotion ->
                    skinMotion.copy(isClicked = false)
                }
                _skinMotions.emit(newList)
                _skinMotionIndex.emit(-1)
            }
            _addMotion.emit(!addMotion.value)
        }
    }

    override fun setFile(skinImgFile: File) {
        _skinImgFile.value = skinImgFile
    }

    override fun makeSkin() {
        viewModelScope.launch {
            val skinImgName: List<String> = listOf(skinImgFile.value!!.name)
            val empty = emptyList<String>()
            val getS3UrlData =
                S3UrlRequest(S3DIRECTORY, skinImgName, empty)
            getUploadUrls(getS3UrlData)

        }
    }

    private fun getUploadUrls(getS3UrlData: S3UrlRequest) {
        viewModelScope.launch {
            s3UrlsGetUseCase(getS3UrlData.toDto()).collect { result ->
                result.onSuccess {
                    Log.d("getUploadUrls", "$it")
                    uploadFilesToS3(skinImgFile.value!!, it.preSignedImageUrls[0])
                }.onFail {
                    Log.d("getUploadUrls", "getUploadUrl 실패")
                }
            }
        }
    }

    private fun uploadFilesToS3(
        skinImgFiles: File,
        preSignedImageUrls: String,
    ) {
        viewModelScope.launch {
            var uploadSuccess = false

            withContext(Dispatchers.IO) {
                try {
                    s3Util.uploadImageWithPresignedUrl(skinImgFiles, preSignedImageUrls)
                    uploadSuccess = true
                } catch (e: Exception) {
                    uploadSuccess = false
                }
            }

            if (uploadSuccess) {
                submitSkin()
            } else {

            }
        }
    }

    private fun submitSkin() {
        if (addMotion.value) {
            val skinMotion = skinMotions.value.find { it.isClicked }
            _skinMotionIndex.value = if (skinMotion != null) skinMotions.value.indexOf(skinMotion) else -1
        }
        viewModelScope.launch {
            capsuleSkinsMakeUseCase(
                CapsuleSkinsMakeRequest(
                    skinName.value,
                    skinImgFile.value!!.name,
                    S3DIRECTORY,
                    skinMotions.value.getOrNull(skinMotionIndex.value)?.motionName,
                    skinMotions.value.getOrNull(skinMotionIndex.value)?.retarget
                ).toDto()
            ).collect { result ->
                result.onSuccess {
                    skinMakeEvent(SkinMakeViewModel.SkinMakeEvent.SuccessSkinMake)
                    Log.d("스킨 생성", "생성 성공")
                }.onFail {
                    Log.d("스킨 생성", "생성 실패")
                }
            }
        }
    }
}