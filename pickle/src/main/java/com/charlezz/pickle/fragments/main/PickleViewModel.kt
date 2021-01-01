package com.charlezz.pickle.fragments.main

import android.app.Application
import androidx.lifecycle.*
import com.charlezz.pickle.util.dagger.AssistedSavedStateViewModelFactory
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import timber.log.Timber

class PickleViewModel @AssistedInject constructor(
    app: Application,
    @Assisted private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(app), LifecycleObserver {

    init {
        Timber.d("init = ${hashCode()}")
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(){

    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
    }

    @AssistedInject.Factory
    interface Factory : AssistedSavedStateViewModelFactory<PickleViewModel>
}