package com.charlezz.pickle.fragments.folder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.charlezz.pickle.data.entity.AlbumItem
import com.charlezz.pickle.util.dagger.AssistedSavedStateViewModelFactory
import com.charlezz.pickle.util.lifecycle.SingleLiveEvent
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PickleAlbumViewModel @AssistedInject constructor(
    val app: Application,
    @Assisted val savedStateHandle: SavedStateHandle
) : AndroidViewModel(app), AlbumItem.OnItemClickListener {

    val items = MutableLiveData<List<AlbumItem>>()

    val itemClickEvent = SingleLiveEvent<AlbumItem>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            PickleAlbumRepository(app).load().map { map ->
                map.values.toList()
                    .sortedByDescending { folder -> folder.order }
                    .map { folder ->
                        AlbumItem(folder, this@PickleAlbumViewModel)
                    }
            }.collectLatest {
                items.postValue(it)
            }
        }
    }

    @AssistedInject.Factory
    interface Factory : AssistedSavedStateViewModelFactory<PickleAlbumViewModel>

    override fun onFolderClick(item: AlbumItem) {
        itemClickEvent.value = item
    }
}