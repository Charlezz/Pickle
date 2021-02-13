package com.charlezz.pickle.fragments.main

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.SharedElementCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.charlezz.pickle.Config
import com.charlezz.pickle.OnImageAppearedListener
import com.charlezz.pickle.PickleSharedViewModel
import com.charlezz.pickle.R
import com.charlezz.pickle.data.entity.CameraItem
import com.charlezz.pickle.data.entity.SystemUIType
import com.charlezz.pickle.databinding.FragmentPickleBinding
import com.charlezz.pickle.uimodel.OptionMenuViewModel
import com.charlezz.pickle.uimodel.ToolbarViewModel
import com.charlezz.pickle.util.DeviceUtil
import com.charlezz.pickle.util.MeasureUtil
import com.charlezz.pickle.util.PickleConstants
import com.charlezz.pickle.util.dagger.SharedViewModelProvider
import com.charlezz.pickle.util.ext.showToast
import com.charlezz.pickle.util.lifecycle.SingleLiveEvent
import com.charlezz.pickle.util.recyclerview.GridSpaceDecoration
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.max


class PickleFragment : DaggerFragment(),
    CameraItem.OnItemClickListener,
    OnImageAppearedListener {


    @Inject
    @SharedViewModelProvider
    lateinit var sharedViewModelProvider: ViewModelProvider

    @Inject
    lateinit var viewModelProvider: ViewModelProvider

    @Inject
    lateinit var adapters: PickleAdapters

    @Inject
    lateinit var gridLayoutManager: Provider<GridLayoutManager>

    @Inject
    lateinit var gridSpaceDecoration: GridSpaceDecoration

    @Inject
    lateinit var config: Config

    @Inject
    lateinit var toolbarViewModel: ToolbarViewModel

    @Inject
    lateinit var optionMenuViewModel: OptionMenuViewModel

    @Inject
    lateinit var systemUIEvent: SingleLiveEvent<SystemUIType>

    private lateinit var sharedViewModel: PickleSharedViewModel

    private lateinit var viewModel: PickleViewModel

    private var _binding: FragmentPickleBinding? = null

    private val binding get() = _binding!!

    private val navigateAlbumEvent = SingleLiveEvent<Unit>()

    private val takePictureLauncher =
        registerForActivityResult(TakePictureContract()) { activityResult: Boolean ->
            if (activityResult) {
                Timber.d("take picture result")
                sharedViewModel.cameraUtil.saveImageToMediaStore {
                    sharedViewModel.repository.invalidate()
                }
            }
        }

    private val writePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            prepareImageCapture()
        }
    }

    private val spanCount by lazy {
        MutableLiveData(
            MeasureUtil.getProperSpanCount(
                requireContext(),
                R.dimen.pickle_column_width
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        this.sharedViewModel = sharedViewModelProvider.get(PickleSharedViewModel::class.java)
        this.viewModel = viewModelProvider.get(PickleViewModel::class.java)
        lifecycleScope.launch {
            sharedViewModel.items.collectLatest {
                adapters.itemAdapter.submitData(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (!config.singleMode) {
            optionMenuViewModel.onCreateOptionMenu(menu)
            this.optionMenuViewModel.apply {
                menuTitle = getString(config.doneTextRes)
                setTitleTextColor(ContextCompat.getColor(context, R.color.point_color))
                setDisableTitleTextColor(Color.parseColor("#aaaaaa"))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPickleBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarBinding.toolbar)
        prepareExitTransitions()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated")
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
        binding.toolbarViewModel = toolbarViewModel
        sharedViewModel.currentDestinationId.value = R.id.pickleFragment

        toolbarViewModel.onTitleClickListener = View.OnClickListener {
            navigateAlbumEvent.call()
        }

        navigateAlbumEvent.observe(viewLifecycleOwner) {
            findNavController().navigate(PickleFragmentDirections.actionPickleFragmentToPickleFolderFragment())
        }
        scrollToPosition()
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressed()
                }
            })

        binding.lifecycleOwner = viewLifecycleOwner
        adapters.itemAdapter.onImageAppearedListener = this
        binding.recyclerView.layoutManager = gridLayoutManager.get()
        binding.recyclerView.addItemDecoration(gridSpaceDecoration)
        binding.recyclerView.setHasFixedSize(true)
        spanCount.observe(viewLifecycleOwner) { onSpanCountChanged(it) }
        adapters.itemAdapter.selection = sharedViewModel.selection

        sharedViewModel.itemClickEvent.observe(viewLifecycleOwner) { triple ->
            triple?.let {
                val view = it.first
                val media = it.second
                val position = it.third
                sharedViewModel.bindingItemAdapterPosition.set(position)

                val navDirection =
                    PickleFragmentDirections.actionPickleFragmentToPickleDetailFragment(
                        media,
                        position
                    )

                if (DeviceUtil.isAndroid5Later()) {
                    if (savedInstanceState == null) {
                        postponeEnterTransition()
                    }
                    findNavController().navigate(
                        navDirection,
                        FragmentNavigatorExtras(view to view.transitionName)
                    )
                } else {
                    findNavController().navigate(navDirection)
                }
            }
        }

        sharedViewModel.itemCount.observe(viewLifecycleOwner) { totalCount ->

        }

        sharedViewModel.selection.getCount().observe(viewLifecycleOwner) { count ->
            optionMenuViewModel.selectedCountString = "$count"
            optionMenuViewModel.selectedCountVisible = count != 0
            optionMenuViewModel.isEnabled = count != 0
        }

        sharedViewModel.currentFolder.observe(viewLifecycleOwner) { folder ->
            toolbarViewModel.title =
                (folder?.name ?: requireContext().getString(config.recentTextRes)) + " ▾"
            if (folder?.bucketId == null && sharedViewModel.cameraUtil.hasCamera()) {
                binding.recyclerView.adapter = adapters.concatedAdapter
            } else {
                binding.recyclerView.adapter = adapters.itemAdapter
            }
        }

        sharedViewModel.singleImageEvent.observe(viewLifecycleOwner) { triple ->
            requireActivity().setResult(Activity.RESULT_OK, Intent().apply {
                val media = triple?.second
                putExtra(PickleConstants.KEY_RESULT_SINGLE, media)
            })
            requireActivity().finish()
        }

        optionMenuViewModel.clickEvent.observe(viewLifecycleOwner) {
            requireActivity().setResult(Activity.RESULT_OK, Intent().apply {
                putParcelableArrayListExtra(
                    PickleConstants.KEY_RESULT_MULTIPLE,
                    ArrayList(sharedViewModel.getSelectedMediaList())
                )
            })
            requireActivity().finish()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onSpanCountChanged(count: Int) {
        Timber.d("onSpanCountChanged:$count")
        val gridLayoutManager = gridLayoutManager.get().apply {
            spanCount = max(count, 1)
        }
        binding.recyclerView.layoutManager = gridLayoutManager
        gridSpaceDecoration.spanCount = count
        binding.recyclerView.invalidateItemDecorations()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Timber.d("onConfigurationChanged# width = ${newConfig.screenWidthDp} height = ${newConfig.screenHeightDp}")
        context?.let {
            spanCount.value = MeasureUtil.getProperSpanCount(it, R.dimen.pickle_column_width)
        }
    }

    override fun onCameraClick() {
        if (DeviceUtil.isAndroid10Later()) {
            prepareImageCapture()
        } else {
            writePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun prepareImageCapture() {
        sharedViewModel.cameraUtil.prepareImageCapture(
            readyToLaunch = { uri -> takePictureLauncher.launch(uri) },
            fallback = { showToast(R.string.pickle_toast_error_file_create) }
        )
    }

    fun onBackPressed() {
        if (sharedViewModel.selection.isEmpty()) {
            requireActivity().finish()
        } else {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.pickle_are_you_sure_to_exit)
                .setPositiveButton(config.confirmTextRes) { _, _ -> requireActivity().finish() }
                .setNegativeButton(config.cancelTextRes) { _, _ -> }
                .show()
        }
    }

    private fun prepareExitTransitions() {
        if (DeviceUtil.isAndroid5Later()) {
            exitTransition = TransitionInflater.from(context)
                .inflateTransition(R.transition.grid_exit_transition)
            setExitSharedElementCallback(
                object : SharedElementCallback() {
                    override fun onMapSharedElements(
                        names: List<String>,
                        sharedElements: MutableMap<String, View>
                    ) {
                        val selectedViewHolder: RecyclerView.ViewHolder =
                            binding.recyclerView.findViewHolderForAdapterPosition(sharedViewModel.bindingItemAdapterPosition.get() + adapters.getHeaderItemCount())
                                ?: return

                        sharedElements[names[0]] =
                            selectedViewHolder.itemView.findViewById(R.id.image)
                    }
                })
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
                    val currentPosition =
                        sharedViewModel.bindingItemAdapterPosition.get() + adapters.getHeaderItemCount()
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

    override fun onImageAppeared(position: Int) {
        if (DeviceUtil.isAndroid5Later()) {
            if (sharedViewModel.bindingItemAdapterPosition.get() != position) {
                return
            }
            startPostponedEnterTransition()
            systemUIEvent.value = SystemUIType.NORMAL
        }
    }
}