package com.charlezz.pickle.fragments.main

import android.Manifest
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.charlezz.pickle.PickleSharedViewModel
import com.charlezz.pickle.R
import com.charlezz.pickle.data.entity.CameraItem
import com.charlezz.pickle.databinding.FragmentPickleBinding
import com.charlezz.pickle.util.DeviceUtil
import com.charlezz.pickle.util.MeasureUtil
import com.charlezz.pickle.util.PickleConstants
import com.charlezz.pickle.util.dagger.SharedViewModelProvider
import com.charlezz.pickle.util.recyclerview.GridSpaceDecoration
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.max


class PickleFragment : DaggerFragment(), CameraItem.OnItemClickListener {


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

    private lateinit var sharedViewModel: PickleSharedViewModel

    private lateinit var viewModel: PickleViewModel

    private lateinit var binding: FragmentPickleBinding

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
        this.sharedViewModel = sharedViewModelProvider.get(PickleSharedViewModel::class.java)
        this.viewModel = viewModelProvider.get(PickleViewModel::class.java)
        lifecycle.addObserver(viewModel)
        lifecycleScope.launch {
            sharedViewModel.items.collectLatest {
                adapters.itemAdapter.submitData(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPickleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated")
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressed()
                }
            })

        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerView.adapter = adapters.concatedAdapter
        binding.recyclerView.layoutManager = gridLayoutManager.get()
        binding.recyclerView.addItemDecoration(gridSpaceDecoration)
        binding.recyclerView.setHasFixedSize(true)
        spanCount.observe(viewLifecycleOwner) { onSpanCountChanged(it) }
        adapters.itemAdapter.selection = sharedViewModel.selection

        sharedViewModel.itemClickEvent.observe(viewLifecycleOwner) { itemAndPosition ->
            itemAndPosition?.let {
                sharedViewModel.bindingItemAdapterPosition.set(it.second)
                findNavController().navigate(
                    PickleFragmentDirections.actionPickleFragmentToPickleDetailFragment(
                        it.first,
                        it.second
                    )
                )
            }
        }

        sharedViewModel.itemCount.observe(viewLifecycleOwner) { count ->
            if (count != null) {
                sharedViewModel.toolbarViewModel.subtitle = resources.getQuantityString(
                    R.plurals.numberOfMedia, count, count
                )
            }
        }

        binding.recyclerView.post {
            val currentPosition =
                sharedViewModel.bindingItemAdapterPosition.getAndSet(PickleConstants.NO_POSITION)
            if (currentPosition != PickleConstants.NO_POSITION) {
                binding.recyclerView.scrollToPosition(currentPosition + adapters.headerItemCount())
            }
        }
    }

    private fun onSpanCountChanged(count: Int) {
        Timber.d("onSpanCountChanged:$count")
        val gridLayoutManager = gridLayoutManager.get().apply {
            spanCount = max(count, 1)
        }
        binding.recyclerView.layoutManager = gridLayoutManager
        binding.recyclerView.addItemDecoration(gridSpaceDecoration.apply {
            spanCount = count
            space = MeasureUtil.dpToPx(requireContext(), 2f)
        })
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
        sharedViewModel.cameraUtil.prepareImageCapture { uri ->
            takePictureLauncher.launch(uri)
        }
    }

    fun onBackPressed() {
        requireActivity().finish()
    }

}