package com.droidblossom.archive.presentation.ui.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.droidblossom.archive.domain.usecase.MemberUseCase
import com.droidblossom.archive.domain.usecase.TestUseCase
import com.droidblossom.archive.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModelImpl @Inject constructor(
    private val memberUsecase : MemberUseCase,
    private val testUseCase : TestUseCase
) : BaseViewModel(), HomeViewModel {

    private val _filterCapsuleSelect: MutableStateFlow<HomeViewModel.CapsuleFilter> =
        MutableStateFlow(HomeViewModel.CapsuleFilter.ALL)
    override val filterCapsuleSelect: StateFlow<HomeViewModel.CapsuleFilter>
        get() = _filterCapsuleSelect

    private val _isClickedFAB: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isClickedFAB: StateFlow<Boolean>
        get() = _isClickedFAB
    private val _existsNotification: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val existsNotification: StateFlow<Boolean>
        get() = _existsNotification
    private val _followLocation: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val followLocation: StateFlow<Boolean>
        get() = _followLocation


    override fun selectPublic() {
        viewModelScope.launch {
            //test
            memberUsecase().collect{
                Log.d("qwer","${it.toString()}")
            }

            if (filterCapsuleSelect.value == HomeViewModel.CapsuleFilter.PUBLIC)
                _filterCapsuleSelect.emit(HomeViewModel.CapsuleFilter.ALL)
            else
                _filterCapsuleSelect.emit(HomeViewModel.CapsuleFilter.PUBLIC)
        }
    }

    override fun selectGroup() {
        viewModelScope.launch {
            if (filterCapsuleSelect.value == HomeViewModel.CapsuleFilter.GROUP)
                _filterCapsuleSelect.emit(HomeViewModel.CapsuleFilter.ALL)
            else
                _filterCapsuleSelect.emit(HomeViewModel.CapsuleFilter.GROUP) }
    }

    override fun selectSecret() {
        viewModelScope.launch {

            //test
            testUseCase().collect{
                Log.d("qwer", "${it}")
            }

            if (filterCapsuleSelect.value == HomeViewModel.CapsuleFilter.SECRET)
                _filterCapsuleSelect.emit(HomeViewModel.CapsuleFilter.ALL)
            else
                _filterCapsuleSelect.emit(HomeViewModel.CapsuleFilter.SECRET) }
    }

    override fun selectHotPlace() {
        viewModelScope.launch {
            if (filterCapsuleSelect.value == HomeViewModel.CapsuleFilter.HOT)
                _filterCapsuleSelect.emit(HomeViewModel.CapsuleFilter.ALL)
            else
                _filterCapsuleSelect.emit(HomeViewModel.CapsuleFilter.HOT) }
    }

    override fun clickFollowBtn() {
        viewModelScope.launch {
            _followLocation.emit(!followLocation.value)
        }
    }

    override fun clickNotificationBtn() {
        viewModelScope.launch {
            _existsNotification.emit(!existsNotification.value)
        }
    }

    override fun clickFAB() {
        viewModelScope.launch { _isClickedFAB.emit(!isClickedFAB.value) }
    }

}