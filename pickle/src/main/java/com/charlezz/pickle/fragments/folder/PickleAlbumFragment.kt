package com.charlezz.pickle.fragments.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.charlezz.pickle.PickleSharedViewModel
import com.charlezz.pickle.R
import com.charlezz.pickle.databinding.FragmentPickleAlbumBinding
import com.charlezz.pickle.util.dagger.SharedViewModelProvider
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import javax.inject.Provider

class PickleAlbumFragment : DaggerFragment() {

    @Inject
    @SharedViewModelProvider
    lateinit var sharedViewModelProvider: ViewModelProvider

    @Inject
    lateinit var gridLayoutManagerProvider: Provider<GridLayoutManager>

    @Inject
    lateinit var adapter: PickleAlbumAdapter

    @Inject
    lateinit var viewModelProvider: ViewModelProvider

    lateinit var binding: FragmentPickleAlbumBinding

    lateinit var viewModel: PickleAlbumViewModel

    lateinit var sharedViewModel: PickleSharedViewModel

    lateinit var gridLayoutManager: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.viewModel = viewModelProvider.get(PickleAlbumViewModel::class.java)
        this.sharedViewModel = sharedViewModelProvider.get(PickleSharedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentPickleAlbumBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.toolbarViewModel.title.value = requireContext().getString(R.string.albums)
        sharedViewModel.toolbarViewModel.subtitle.value = null

        binding.lifecycleOwner
        binding.recyclerView.layoutManager =
            gridLayoutManagerProvider.get().also { gridLayoutManager ->
                this.gridLayoutManager = gridLayoutManager
            }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)

        viewModel.items.observe(viewLifecycleOwner) { items ->
            adapter.submitItems(items)
        }

        viewModel.itemClickEvent.observe(viewLifecycleOwner) { item ->
            sharedViewModel.setBucketId(item?.album)
            findNavController().navigateUp()
        }

    }
}