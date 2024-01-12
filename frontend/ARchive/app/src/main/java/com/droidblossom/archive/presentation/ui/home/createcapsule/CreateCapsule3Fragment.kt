package com.droidblossom.archive.presentation.ui.home.createcapsule

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.droidblossom.archive.R
import com.droidblossom.archive.databinding.FragmentCreateCapsule3Binding
import com.droidblossom.archive.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateCapsule3Fragment :
    BaseFragment<CreateCapsuleViewModelImpl, FragmentCreateCapsule3Binding>(R.layout.fragment_create_capsule3) {

    override val viewModel: CreateCapsuleViewModelImpl by activityViewModels()

    override fun observeData() {
    }

}