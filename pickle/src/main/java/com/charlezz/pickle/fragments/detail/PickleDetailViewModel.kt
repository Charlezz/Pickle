package com.charlezz.pickle.fragments.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.charlezz.pickle.data.entity.MediaItem
import com.charlezz.pickle.util.dagger.AssistedSavedStateViewModelFactory
import com.charlezz.pickle.util.lifecycle.SingleLiveEvent
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import timber.log.Timber

class PickleDetailViewModel @AssistedInject constructor(
    app: Application,
    @Assisted private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(app) {

    var checkBoxEnabled = MutableLiveData(true)
    val isChecked = MutableLiveData<Boolean>()
    var currentMediaItem: MediaItem? = null
    val checkBoxClickEvent = SingleLiveEvent<MediaItem?>()

    fun onCheckBoxClick() {
        Timber.d("onCheckBoxClick")
        checkBoxClickEvent.value = currentMediaItem
    }

    @AssistedInject.Factory
    interface Factory : AssistedSavedStateViewModelFactory<PickleDetailViewModel>
}