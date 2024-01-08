package com.droidblossom.archive.presentation.ui.skin.skinmake

import android.net.Uri
import com.droidblossom.archive.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SkinMakeViewModelImpl @Inject constructor() : BaseViewModel(), SkinMakeViewModel {
    override val imgUri = MutableStateFlow<Uri?>(null)

}