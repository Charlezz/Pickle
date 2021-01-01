package com.charlezz.pickle.fragments.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.charlezz.pickle.PickleSharedViewModel
import com.charlezz.pickle.databinding.FragmentPickleDetailBinding
import com.charlezz.pickle.util.DeviceUtil
import com.charlezz.pickle.util.MeasureUtil
import com.charlezz.pickle.util.PickleConstants
import com.charlezz.pickle.util.dagger.SharedViewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

class PickleDetailFragment : DaggerFragment() {

    companion object {
        const val DEFAULT_CHECK_BOX_TOP_MARGIN = 10f
    }

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

    private lateinit var binding: FragmentPickleDetailBinding

    private lateinit var sharedViewModel: PickleSharedViewModel

    private lateinit var viewModel: PickleDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.sharedViewModel = sharedViewModelProvider.get(PickleSharedViewModel::class.java)
        this.viewModel = viewModelProvider.get(PickleDetailViewModel::class.java)

        adapter.registerAdapterDataObserver(object:RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.recyclerView.scrollToPosition(sharedViewModel.bindingItemAdapterPosition.getAndSet(PickleConstants.NO_POSITION))
            }
        })

        adapter.selection = sharedViewModel.selection

        lifecycleScope.launchWhenCreated {
            sharedViewModel.items.collectLatest { adapter.submitData(it) }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPickleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated")
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = linearLayoutManager.get()
        binding.recyclerView.addOnScrollListener(onScrollListener)
        pagerSnapHelper.attachToRecyclerView(binding.recyclerView)

        viewModel.checkBoxClickEvent.observe(viewLifecycleOwner) { item ->
            item?.let {
                sharedViewModel.selection.toggle(item.getId(), item.media)
                viewModel.isChecked.value = sharedViewModel.selection.isSelected(item.getId())
            } ?: Timber.d("No Item")
        }

        if (DeviceUtil.isAndroid5Later()) {
            ViewCompat.setOnApplyWindowInsetsListener(binding.checked) { v, insets ->
                viewModel.guideTopMargin.value =
                    insets.systemWindowInsetTop + MeasureUtil.getToolBarHeight(v.context)
                insets
            }
        } else {
            viewModel.guideTopMargin.value =
                MeasureUtil.dpToPx(requireContext(), DEFAULT_CHECK_BOX_TOP_MARGIN)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,onBackPressedCallback)

    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        fun onScrolled(){
            val currentPosition =
                (binding.recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

            if (currentPosition != RecyclerView.NO_POSITION) {
                viewModel.currentMediaItem = adapter.peek(currentPosition)
                viewModel.currentMediaItem?.let { item ->
                    viewModel.isChecked.value = sharedViewModel.selection.isSelected(item.getId())
                }
                sharedViewModel.toolbarViewModel.subtitle =
                    "${currentPosition + 1} / ${sharedViewModel.itemCount.value}"
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

    private val onBackPressedCallback = object:OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
            val position = layoutManager.findFirstCompletelyVisibleItemPosition()
            sharedViewModel.bindingItemAdapterPosition.set(position)
            Timber.d("save position = $position")
            findNavController().navigateUp()
        }
    }

}