package com.charlezz.pickle

import android.app.Application
import android.net.Uri
import android.view.View
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.charlezz.pickle.data.entity.Album
import com.charlezz.pickle.data.entity.Media
import com.charlezz.pickle.data.entity.MediaItem
import com.charlezz.pickle.fragments.main.AppPickleRepository
import com.charlezz.pickle.fragments.main.PicklePagingSource
import com.charlezz.pickle.fragments.main.PickleRepository
import com.charlezz.pickle.util.CameraUtil
import com.charlezz.pickle.util.PickleConstants
import com.charlezz.pickle.util.dagger.AssistedSavedStateViewModelFactory
import com.charlezz.pickle.util.lifecycle.SingleLiveEvent
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger


@ExperimentalCoroutinesApi
class PickleSharedViewModel @AssistedInject constructor(
    val app: Application,
    val config: Config,
    @Assisted private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(app),
    LifecycleObserver,
    MediaItem.OnItemClickListener {

    companion object {
        const val KEY_FOLDER = "key_folder"
        const val KEY_SAVED_SELECTION = "key_saved_selection"
        const val KEY_SAVED_IMAGE_PATH = "KEY_SAVED_IMAGE_PATH"
    }

    val repository: PickleRepository = AppPickleRepository(app)

    val cameraUtil: CameraUtil = CameraUtil(app, config.environmentDir, config.dirToSave).apply {
        val savedImagePath = savedStateHandle.get<String>(KEY_SAVED_IMAGE_PATH)
        Timber.d("savedImagePath = $savedImagePath")
        currentImagePath = savedImagePath
    }

    val selection: Selection = savedStateHandle.get<Selection>(KEY_SAVED_SELECTION) ?: Selection()

    val bindingItemAdapterPosition = AtomicInteger(PickleConstants.NO_POSITION)

    val itemClickEvent = SingleLiveEvent<Triple<View, Media, Int>?>()

    val items: Flow<PagingData<MediaItem>> = savedStateHandle.getLiveData<Album>(KEY_FOLDER)
        .asFlow()
        .flatMapLatest { album: Album? ->
            repository.getItems(
                PicklePagingSource.SelectionType.IMAGE_AND_VIDEO,
                album?.bucketId,
                PickleConstants.DEFAULT_POSITION,
                PickleConstants.DEFAULT_PAGE_SIZE
            ).map { pagingData ->
                pagingData.map { media -> MediaItem(media, config, this) }
            }
        }
        .cachedIn(viewModelScope)

    val itemCount = repository.getCount()

    val currentFolder = savedStateHandle.getLiveData<Album>(KEY_FOLDER)

    val currentDestinationId = SingleLiveEvent<Int>()

    init {
        Timber.d("init = ${this.hashCode()}")
        if (!savedStateHandle.contains(KEY_FOLDER)) {
            savedStateHandle.set(KEY_FOLDER, null)
        }
    }

    override fun onItemClick(view: View, item: MediaItem, position: Int) {
        val triple = Triple(view, item.media, position)
        itemClickEvent.value = triple

    }

    override fun onCheckBoxClick(item: MediaItem) {
        selection.toggle(item.getId(), item.media)
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
        savedStateHandle.set(KEY_SAVED_SELECTION, selection)
        savedStateHandle.set(KEY_SAVED_IMAGE_PATH, cameraUtil.currentImagePath)
    }

    fun getSelectedMediaList(): List<Media> {
        return selection.toList()
            .filter { media -> validateIfExist(media.getUri()) }
    }

    private fun validateIfExist(uri: Uri): Boolean {
        return try {
            app.contentResolver.openInputStream(uri)?.use { it.close() }
            true
        } catch (e: IOException) {
            Timber.e(e)
            false
        }
    }

    fun setBucketId(album: Album?) {
        savedStateHandle.set(KEY_FOLDER, album)
    }

//    fun refresh(){
//        setBucketId(savedStateHandle.get<Album>(KEY_FOLDER))
//    }

    @AssistedInject.Factory
    interface Factory : AssistedSavedStateViewModelFactory<PickleSharedViewModel>

}