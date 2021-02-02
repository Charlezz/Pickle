package com.charlezz.pickle.fragments.detail

import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.SharedElementCallback
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.charlezz.pickle.OnImageAppearedListener
import com.charlezz.pickle.PickleSharedViewModel
import com.charlezz.pickle.R
import com.charlezz.pickle.data.entity.SystemUIType
import com.charlezz.pickle.databinding.FragmentPickleDetailBinding
import com.charlezz.pickle.uimodel.OptionMenuViewModel
import com.charlezz.pickle.uimodel.ToolbarViewModel
import com.charlezz.pickle.util.DeviceUtil
import com.charlezz.pickle.util.dagger.SharedViewModelProvider
import com.charlezz.pickle.util.ext.setMarginBottom
import com.charlezz.pickle.util.ext.setMarginLeft
import com.charlezz.pickle.util.ext.setMarginRight
import com.charlezz.pickle.util.ext.setMarginTop
import com.charlezz.pickle.util.lifecycle.SingleLiveEvent
import com.github.chrisbanes.photoview.PhotoView
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

class PickleDetailFragment : DaggerFragment(), OnImageAppearedListener {

    @Inject
    @SharedViewModelProvider
    lateinit var sharedViewModelProvider: ViewModelProvider

    @Inject
    lateinit var viewModelProvider: ViewModelProvider

    @Inject
    lateinit var adapter: PickleDetailAdapter

    @Inject
    lateinit var linearLayoutManager: Provider<LinearLayoutManager>

    @Inject
    lateinit var pagerSnapHelper: PagerSnapHelper

    @Inject
    lateinit var toolbarViewModel: ToolbarViewModel

    @Inject
    lateinit var systemUIEvent: SingleLiveEvent<SystemUIType>

    @Inject
    lateinit var optionMenuViewModel: OptionMenuViewModel

    private val binding get() = _binding!!

    private var _binding: FragmentPickleDetailBinding? = null

    private lateinit var sharedViewModel: PickleSharedViewModel

    private lateinit var viewModel: PickleDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        this.sharedViewModel = sharedViewModelProvider.get(PickleSharedViewModel::class.java)
        this.viewModel = viewModelProvider.get(PickleDetailViewModel::class.java)

        adapter.selection = sharedViewModel.selection

        lifecycleScope.launchWhenCreated {
            sharedViewModel.items.collectLatest { adapter.submitData(it) }
        }

        if (DeviceUtil.isAndroid5Later()) {
            prepareSharedElementTransition()
            if (savedInstanceState == null) {
                postponeEnterTransition()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        optionMenuViewModel.onCreateOptionMenu(menu)
        optionMenuViewModel.apply {
            menuTitle = getString(R.string.done)
            setTitleTextColor(ContextCompat.getColor(context, R.color.point_color))
            setDisableTitleTextColor(Color.parseColor("#ffffff"))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPickleDetailBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarBinding.toolbar)

//        prepareSharedElementTransition()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated")
        scrollToPosition()
        binding.toolbarViewModel = toolbarViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
        adapter.onImageAppearedListener = this
        binding.recyclerView.layoutManager = linearLayoutManager.get()
        binding.recyclerView.addOnScrollListener(onScrollListener)
        pagerSnapHelper.attachToRecyclerView(binding.recyclerView)

        viewModel.checkBoxClickEvent.observe(viewLifecycleOwner) { item ->
            item?.let {
                sharedViewModel.selection.toggle(item.getId(), item.media)
                viewModel.isChecked.value = sharedViewModel.selection.isSelected(item.getId())
            } ?: Timber.d("No Item")
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )

        //safe area

        ViewCompat.setOnApplyWindowInsetsListener(binding.safeArea) { _, insets ->
            binding.safeArea.setMarginTop(0)
            binding.safeArea.setMarginBottom(insets.systemWindowInsetBottom)
            binding.safeArea.setMarginLeft(insets.systemWindowInsetLeft)
            binding.safeArea.setMarginRight(insets.systemWindowInsetRight)
            insets
        }

        sharedViewModel.itemClickEvent.observe(viewLifecycleOwner) {
            viewModel.crossfade()
            /**
             * PhotoView 라이브러리가 onLayout() 호출시 Matrix 상태를 보존하지 않으므로
             * 직접 저장/복원 한다.
             */
            val photoView: PhotoView? = it?.first as PhotoView?
            val matrix = Matrix()
            photoView?.attacher?.getSuppMatrix(matrix)
            photoView?.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(
                    v: View?,
                    left: Int,
                    top: Int,
                    right: Int,
                    bottom: Int,
                    oldLeft: Int,
                    oldTop: Int,
                    oldRight: Int,
                    oldBottom: Int
                ) {
                    photoView.attacher?.setDisplayMatrix(matrix)
                    photoView.removeOnLayoutChangeListener(this)
                }
            })

            /**
             * decorView를 수정하면, 하위 View들의 레이아웃을 다시 계산하게 된다.
             */
            if (viewModel.fullScreen.value == true) {
                systemUIEvent.value = SystemUIType.FULLSCREEN_WITHOUT_SYSTEM_UI
            } else {
                systemUIEvent.value = SystemUIType.FULLSCREEN
            }
        }

        sharedViewModel.selection.getCount().observe(viewLifecycleOwner) { count ->
            optionMenuViewModel.selectedCountString = "$count"
            optionMenuViewModel.selectedCountVisible = count != 0
            optionMenuViewModel.isEnabled = count != 0
        }

    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        fun onScrolled() {
            val currentPosition =
                (binding.recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

            if (currentPosition != RecyclerView.NO_POSITION) {
                viewModel.currentMediaItem = adapter.peek(currentPosition)
                viewModel.currentMediaItem?.let { item ->
                    viewModel.isChecked.value = sharedViewModel.selection.isSelected(item.getId())
                    toolbarViewModel.title =
                        "${currentPosition + 1} / ${sharedViewModel.itemCount.value}"
                }
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            viewModel.checkBoxEnabled.value = (newState == RecyclerView.SCROLL_STATE_IDLE)
            onScrolled()
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            onScrolled()
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
            val position = layoutManager.findFirstCompletelyVisibleItemPosition()
            sharedViewModel.bindingItemAdapterPosition.set(position)
            Timber.d("save position = $position")
            findNavController().navigateUp()
        }
    }

    private fun scrollToPosition() {
        binding.recyclerView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                binding.recyclerView.removeOnLayoutChangeListener(this)
                val layoutManager = binding.recyclerView.layoutManager
                layoutManager?.let {
                    val currentPosition = sharedViewModel.bindingItemAdapterPosition.get()
                    val viewAtPosition = layoutManager.findViewByPosition(currentPosition)
                    if (viewAtPosition == null || layoutManager.isViewPartiallyVisible(
                            viewAtPosition,
                            false,
                            true
                        )
                    ) {
                        binding.recyclerView.post { layoutManager.scrollToPosition(currentPosition) }
                    }
                }

            }
        })
    }

    private fun prepareSharedElementTransition() {
        if (DeviceUtil.isAndroid5Later()) {
//            val transition = TransitionInflater.from(context)
//                .inflateTransition(R.transition.image_shared_element_transition)
//            sharedElementEnterTransition = transition

            sharedElementEnterTransition = TransitionInflater.from(context)
                .inflateTransition(R.transition.image_shared_element_transition)

            setEnterSharedElementCallback(
                object : SharedElementCallback() {
                    override fun onMapSharedElements(
                        names: List<String>,
                        sharedElements: MutableMap<String, View>
                    ) {
                        val selectedViewHolder: RecyclerView.ViewHolder =
                            binding.recyclerView.findViewHolderForAdapterPosition(sharedViewModel.bindingItemAdapterPosition.get())
                                ?: return

                        sharedElements[names[0]] =
                            selectedViewHolder.itemView.findViewById(R.id.image)
                    }
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val currentPosition =
            (binding.recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        sharedViewModel.bindingItemAdapterPosition.set(currentPosition)
        this._binding = null
    }

    override fun onImageAppeared(position: Int) {
        if (DeviceUtil.isAndroid5Later()) {
            if (sharedViewModel.bindingItemAdapterPosition.get() != position) {
                return
            }

            startPostponedEnterTransition()
            sharedViewModel.currentDestinationId.value = R.id.pickleDetailFragment
            systemUIEvent.value = SystemUIType.FULLSCREEN

            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
                when (view.display.rotation) {
                    Surface.ROTATION_90 -> {
                        binding.toolbarBinding.toolbar.setMarginLeft(0)
                        binding.toolbarBinding.toolbar.setMarginRight(insets.systemWindowInsetRight)
                    }
                    Surface.ROTATION_270 -> {
                        binding.toolbarBinding.toolbar.setMarginLeft(insets.systemWindowInsetLeft)
                        binding.toolbarBinding.toolbar.setMarginRight(0)
                    }
                    else -> {
                        binding.toolbarBinding.toolbar.setMarginLeft(insets.systemWindowInsetLeft)
                        binding.toolbarBinding.toolbar.setMarginRight(insets.systemWindowInsetRight)
                    }
                }
                binding.toolbarBinding.toolbar.setMarginTop(insets.systemWindowInsetTop)
                binding.toolbarBinding.toolbar.setMarginBottom(insets.systemWindowInsetBottom)
                insets
            }
        }
    }

}