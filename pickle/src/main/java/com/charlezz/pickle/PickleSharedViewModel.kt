package com.charlezz.pickle

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.charlezz.pickle.dagger.AssistedSavedStateViewModelFactory
import com.charlezz.pickle.data.entity.Media
import com.charlezz.pickle.data.entity.MediaItem
import com.charlezz.pickle.data.repository.PicklePagingSource
import com.charlezz.pickle.data.repository.PickleRepository
import com.charlezz.pickle.util.lifecycle.SingleLiveEvent
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber


class PickleSharedViewModel @AssistedInject constructor(
    app: Application,
    val repository: PickleRepository,
    val cameraUtil: CameraUtil,
    val selection: Selection<Media>,
    @Assisted private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(app),
    LifecycleObserver,
    MediaItem.OnItemClickListener {

    companion object {
        const val KEY_POSITION = "key_position"
    }

    private val uriObserver = Observer<Uri?>{
        Timber.i("uriObserver = $it")
        repository.invalidate()
    }

    private val clearListCh = Channel<Unit>(Channel.CONFLATED)

    val itemClickEvent = SingleLiveEvent<MediaItem>()

    val items: Flow<PagingData<MediaItem>> = flowOf(
        clearListCh.receiveAsFlow().map { PagingData.empty() },
        savedStateHandle.getLiveData<Int>(KEY_POSITION)
            .asFlow()
            .flatMapLatest { position ->
                repository.getItems(
                    PicklePagingSource.SelectionType.IMAGE_AND_VIDEO,
                    null,
                    position,
                    PickleConstants.DEFAULT_PAGE_SIZE
                ).map { pagingData ->
                    pagingData.map { media -> MediaItem(media, this) }
                }
            }
            .cachedIn(viewModelScope)
    ).flattenMerge(2)

    init {
        Timber.i("init = ${this.hashCode()}")
        if (!savedStateHandle.contains(KEY_POSITION)) {
            savedStateHandle.set(KEY_POSITION, PickleConstants.DEFAULT_POSITION)
        }
    }

    override fun onItemClick(item: MediaItem) {
        selection.toggle(item.getId(), item.media)
        itemClickEvent.value = item
        item.notifyChange()

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        Timber.d("onResume")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        Timber.d("onPause")
        saveState()
    }

    private fun saveState() {

    }

    override fun onCleared() {
        super.onCleared()
        repository.close()
    }

    fun getSelectedMediaList(): List<Media> {
        return selection.toList()
    }

    @AssistedInject.Factory
    interface Factory : AssistedSavedStateViewModelFactory<PickleSharedViewModel>

}