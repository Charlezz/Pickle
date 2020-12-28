package com.charlezz.pickle

import android.Manifest
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.charlezz.pickle.dagger.SharedViewModelProvider
import com.charlezz.pickle.data.entity.CameraItem
import com.charlezz.pickle.databinding.FragmentPickleBinding
import com.charlezz.pickle.util.DeviceUtil
import com.charlezz.pickle.util.ScreenUtil
import com.charlezz.pickle.util.recyclerview.GridSpaceDecoration
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.max


class PickleFragment : DaggerFragment(), CameraItem.OnItemClickListener {

    @Inject
    @SharedViewModelProvider
    lateinit var sharedViewModelProvider: ViewModelProvider

    @Inject
    lateinit var adapters: PickleAdapters

    @Inject
    lateinit var gridLayoutManager: Provider<GridLayoutManager>

    @Inject
    lateinit var gridSpaceDecoration: GridSpaceDecoration

    private lateinit var sharedViewModel: PickleSharedViewModel

    private lateinit var binding: FragmentPickleBinding

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { activityResult: Boolean ->
        if (activityResult) {
            sharedViewModel.cameraUtil.saveImageToMediaStore()
            {
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
            ScreenUtil.getProperSpanCount(
                requireContext(),
                R.dimen.pickle_column_width
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.sharedViewModel = sharedViewModelProvider.get(PickleSharedViewModel::class.java)
        lifecycleScope.launchWhenCreated {
            sharedViewModel.items.collectLatest { adapters.itemAdapter.submitData(it) }
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
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerView.adapter = adapters.concatAdapter
        binding.recyclerView.layoutManager = gridLayoutManager.get()
        binding.recyclerView.addItemDecoration(gridSpaceDecoration)
        spanCount.observe(viewLifecycleOwner, this::onSpanCountChanged)
        sharedViewModel.itemClickEvent.observe(viewLifecycleOwner) { item ->
            //nothing to do
        }
        adapters.itemAdapter.selection = sharedViewModel.selection

    }

    private fun onSpanCountChanged(count: Int) {
        Timber.i("onSpanCountChanged:$count")
        val gridLayoutManager = gridLayoutManager.get().apply {
            spanCount = max(count, 1)
        }
        binding.recyclerView.layoutManager = gridLayoutManager
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Timber.i("onConfigurationChanged# width = ${newConfig.screenWidthDp} height = ${newConfig.screenHeightDp}")
        context?.let {
            spanCount.value = ScreenUtil.getProperSpanCount(it, R.dimen.pickle_column_width)
        }
    }

    override fun onCameraClick() {
        if(DeviceUtil.isAndroid10Later()){
            prepareImageCapture()
        }else{
            writePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun prepareImageCapture(){
        sharedViewModel.cameraUtil.prepareImageCapture {uri->
            takePictureLauncher.launch(uri)
        }
    }
}