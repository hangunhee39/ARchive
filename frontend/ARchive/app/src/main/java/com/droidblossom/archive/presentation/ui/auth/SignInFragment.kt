package com.droidblossom.archive.presentation.ui.auth

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.droidblossom.archive.R
import com.droidblossom.archive.databinding.FragmentSignInBinding
import com.droidblossom.archive.domain.model.auth.SignUp
import com.droidblossom.archive.domain.model.member.CheckStatus
import com.droidblossom.archive.presentation.base.BaseFragment
import com.droidblossom.archive.presentation.customview.PermissionDialogButtonClickListener
import com.droidblossom.archive.presentation.customview.PermissionDialogFragment
import com.droidblossom.archive.presentation.ui.MainActivity
import com.droidblossom.archive.util.SocialLoginUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : BaseFragment<AuthViewModelImpl,FragmentSignInBinding>(R.layout.fragment_sign_in) {


    lateinit var navController: NavController

    override val viewModel : AuthViewModelImpl by activityViewModels()

    private lateinit var socialLoginUtil: SocialLoginUtil

    private var googleLoginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            socialLoginUtil.handleGoogleSignInResult(task)
        }
    }

    private val essentialPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.all { it.value } -> {
                    MainActivity.goMain(requireContext())
                }

                else -> {
                    handleEssentialPermissionsDenied()
                }
            }

        }

    private fun handleEssentialPermissionsDenied() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) || shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            showToastMessage("ARchive 앱을 사용하려면 카메라, 위치 권한은 필수입니다.")
        } else {
            showSettingsDialog(
                PermissionDialogFragment.PermissionType.ESSENTIAL,
                object : PermissionDialogButtonClickListener {
                    override fun onLeftButtonClicked() {
                        showToastMessage("ARchive 앱을 사용하려면 카메라, 위치 권한은 필수입니다.")
                        requireActivity().finish()
                    }

                    override fun onRightButtonClicked() {
                        navigateToAppSettings{essentialPermissionLauncher.launch(essentialPermissionList)}
                    }

                })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        socialLoginUtil = SocialLoginUtil(requireContext(), object : SocialLoginUtil.LoginCallback {
            override fun onLoginSuccess(memberStatusCheckData : CheckStatus,signUpData : SignUp) {
                viewModel.memberStatusCheck(memberStatusCheckData, signUpData)
            }

            override fun onLoginFailure(error: Throwable) {
                // 에러 처리 로직
            }
        })

        binding.kakaoLoginBtn.setOnClickListener {
            socialLoginUtil.kakaoSignIn()
        }

        binding.googleLoginBtn.setOnClickListener {
            val signInIntent = socialLoginUtil.googleSignIn()
            googleLoginLauncher.launch(signInIntent)
        }
    }

    override fun observeData() {

        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signInEvents.collect { event ->
                    when (event) {

                        is AuthViewModel.SignInEvent.NavigateToMain -> {
                            essentialPermissionLauncher.launch(essentialPermissionList)
                        }

                        is AuthViewModel.SignInEvent.NavigateToSignUp -> {
                            // 회원가입 화면으로 이동
                            if(navController.currentDestination?.id != R.id.signUpFragment) {
                                navController.navigate(R.id.action_signInFragment_to_signUpFragment)
                            }
                        }

                        is AuthViewModel.SignInEvent.ShowToastMessage -> {
                            showToastMessage(event.message)
                        }

                    }
                }
            }
        }
    }

}