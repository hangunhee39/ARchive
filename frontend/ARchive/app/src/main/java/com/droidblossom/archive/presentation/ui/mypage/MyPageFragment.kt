package com.droidblossom.archive.presentation.ui.mypage

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.droidblossom.archive.R
import com.droidblossom.archive.databinding.FragmentMyPageBinding
import com.droidblossom.archive.presentation.base.BaseFragment
import com.droidblossom.archive.presentation.ui.capsule.CapsuleDetailActivity
import com.droidblossom.archive.presentation.ui.home.dialog.CapsulePreviewDialogFragment
import com.droidblossom.archive.presentation.ui.mypage.adapter.CapsuleRVA
import com.droidblossom.archive.presentation.ui.mypage.adapter.ProfileRVA
import com.droidblossom.archive.presentation.ui.mypage.adapter.SpinnerAdapter
import com.droidblossom.archive.presentation.ui.mypage.friend.FriendActivity
import com.droidblossom.archive.presentation.ui.mypage.friendaccept.FriendAcceptActivity
import com.droidblossom.archive.presentation.ui.mypage.setting.SettingActivity
import com.droidblossom.archive.util.CustomLifecycleOwner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MyPageFragment : BaseFragment<MyPageViewModelImpl, FragmentMyPageBinding>(R.layout.fragment_my_page) {

    override val viewModel: MyPageViewModelImpl by viewModels()
    private var reloadMyInfo = false

    private val visibleLifecycleOwner: CustomLifecycleOwner by lazy {
        CustomLifecycleOwner()
    }

    private val profileRVA: ProfileRVA by lazy {
        ProfileRVA(
            {
                startActivity(FriendActivity.newIntent(requireContext(), FriendActivity.GROUP))
                reloadMyInfo = true
            },
            {
                startActivity(FriendActivity.newIntent(requireContext(), FriendActivity.FRIEND))
                reloadMyInfo = true
            },
            {
                startActivity(
                    FriendAcceptActivity.newIntent(
                        requireContext(),
                        FriendAcceptActivity.FRIEND
                    )
                )
                reloadMyInfo = true
            },
            {
                startActivity(SettingActivity.newIntent(requireContext()))
            }
        )
    }
    private val capsuleRVA: CapsuleRVA by lazy {
        CapsuleRVA(
            { id, type ->
                startActivity(
                    CapsuleDetailActivity.newIntent(
                        requireContext(),
                        id,
                        type
                    )
                )
            },
            { capsuleIndex, id, type ->
                val existingDialog =
                    parentFragmentManager.findFragmentByTag(CapsulePreviewDialogFragment.TAG) as DialogFragment?
                if (existingDialog == null) {
                    val dialog = CapsulePreviewDialogFragment.newInstance(
                        capsuleIndex.toString(),
                        id.toString(),
                        type.toString(),
                        false
                    )
                    dialog.show(parentFragmentManager, CapsulePreviewDialogFragment.TAG)
                }

            }
        )
    }

    private val capsuleTypes = arrayOf(
        SpinnerCapsuleType.SECRET, SpinnerCapsuleType.PUBLIC, SpinnerCapsuleType.GROUP
    )

    private val spinnerA: SpinnerAdapter by lazy {
        SpinnerAdapter(
            requireContext(),
            capsuleTypes
        ) { capsuleTypes ->
            viewModel.selectSpinnerItem(capsuleTypes)

        }
    }

    private val concatAdapter: ConcatAdapter by lazy {
        val config = ConcatAdapter.Config.Builder()
            .setIsolateViewTypes(true)
            .setStableIdMode(ConcatAdapter.Config.StableIdMode.ISOLATED_STABLE_IDS)
            .build()
        ConcatAdapter(config, profileRVA, spinnerA, capsuleRVA)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener(
            "capsuleState",
            viewLifecycleOwner
        ) { key, bundle ->
            val capsuleIndex = bundle.getInt("capsuleIndex")
            val capsuleId = bundle.getLong("capsuleId")
            val capsuleOpenState = bundle.getBoolean("isOpened")
            if (capsuleIndex != -1 && capsuleOpenState) {
                viewModel.updateCapsuleOpenState(capsuleIndex, capsuleId)
                capsuleRVA.notifyItemChanged(capsuleIndex)
            }
        }
        initCustomLifeCycle()
        initMyPageRVA()
        val layoutParams =
            binding.myPageSwipeRefreshLayout.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.topMargin += getStatusBarHeight()
        binding.myPageSwipeRefreshLayout.layoutParams = layoutParams
    }

    private fun initMyPageRVA() {
        val layoutManager = GridLayoutManager(requireContext(), 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                var totalItemsCounted = 0
                concatAdapter.adapters.forEach { adapter ->
                    val itemCount = adapter.itemCount
                    if (position < totalItemsCounted + itemCount) {
                        val localPosition = position - totalItemsCounted
                        val viewType = adapter.getItemViewType(localPosition)
                        return when (viewType) {
                            PROFILE_TYPE -> 3
                            CAPSULE_TYPE -> 1
                            else -> 3
                        }
                    }
                    totalItemsCounted += itemCount
                }
                return 3
            }
        }
        binding.myPageRV.layoutManager = layoutManager
        binding.myPageRV.adapter = concatAdapter
        binding.myPageSwipeRefreshLayout.setOnRefreshListener {
            viewModel.load()
        }

        binding.myPageRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = recyclerView.adapter!!.itemCount
                val threshold = 6

                if (lastVisibleItemPosition >= totalItemCount - threshold) {
                    viewModel.onScrollNearBottom()
                }
            }
        })

    }

    override fun observeData() {
        visibleLifecycleOwner.lifecycleScope.launch {
            visibleLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.myCapsules.collect { capsule ->
                    if (viewModel.clearCapsule) {
                        viewModel.clearCapsule = false
                    } else {
                        viewModel.updateMyCapsulesUI()
                    }
                }
            }
        }
        visibleLifecycleOwner.lifecycleScope.launch {
            visibleLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.myCapsulesUI.collect { capsule ->
                    capsuleRVA.submitList(capsule) {
                        if (binding.myPageSwipeRefreshLayout.isRefreshing) {
                            binding.myPageSwipeRefreshLayout.isRefreshing = false
                            binding.myPageRV.scrollToPosition(0)
                        }
                    }
                }
            }
        }

        visibleLifecycleOwner.lifecycleScope.launch {
            visibleLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.myPageEvents.collect { event ->
                    when (event) {
                        is MyPageViewModel.MyPageEvent.ShowToastMessage -> {
                            showToastMessage(event.message)
                        }

                        is MyPageViewModel.MyPageEvent.ClickSetting -> {
                            startActivity(SettingActivity.newIntent(requireContext()))
                        }

                        is MyPageViewModel.MyPageEvent.HideLoading -> {
                            binding.myPageSwipeRefreshLayout.isRefreshing = false
                        }

                        else -> {}
                    }
                }
            }
        }

        visibleLifecycleOwner.lifecycleScope.launch {
            visibleLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.myInfo.collect { memberDetail ->
                    profileRVA.submitList(listOf(memberDetail.toUIModel()))
                }
            }
        }

        visibleLifecycleOwner.lifecycleScope.launch {
            visibleLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.capsuleType.collect {
                    if (viewModel.viewModelReload){
                        viewModel.clearCapsules(false)
                    }

                }
            }
        }
    }

    private fun initCustomLifeCycle() {
        viewLifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_START,
                    Lifecycle.Event.ON_CREATE,
                    Lifecycle.Event.ON_RESUME,
                    Lifecycle.Event.ON_PAUSE, -> {
                        if (isHidden) {
                            visibleLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
                        } else {
                            visibleLifecycleOwner.handleLifecycleEvent(event)
                        }
                    }
                    else -> {
                        visibleLifecycleOwner.handleLifecycleEvent(event)
                    }
                }
            }
        })
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            onHidden()
        } else {
            onShow()
        }
    }

    private fun onHidden() {
        viewModel.viewModelReload = false
        reloadState = true
    }

    private fun onShow() {
        visibleLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        if (reload){
            viewModel.load()
            reload = false
            viewModel.viewModelReload = false
            binding.myPageRV.scrollToPosition(0)
        }
        if (!reload && !reloadState){
            viewModel.load()
            reload = false
            viewModel.viewModelReload = false
            binding.myPageRV.scrollToPosition(0)
        }

        if (reloadMyInfo){
            viewModel.getMe()
        }
    }

    override fun onResume() {
        super.onResume()
        reloadState = false
        reload = false
    }

    companion object {

        const val TAG = "MY"
        const val PROFILE_TYPE = 1
        const val STORY_TYPE = 2
        const val SPINNER_TYPE = 3
        const val CAPSULE_TYPE = 4

        private var reloadState = true
        private var reload = false
        fun newIntent() : MyPageFragment{
            if (reloadState){
                reload = true
            }
            return MyPageFragment()
        }
    }

    enum class SpinnerCapsuleType(val description: String) {
        SECRET("Secret"),
        PUBLIC("Public"),
        GROUP("Group")
    }
}