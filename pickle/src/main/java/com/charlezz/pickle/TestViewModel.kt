package com.charlezz.pickle

import androidx.lifecycle.*
import timber.log.Timber

class TestViewModel(val savedStateHandle:SavedStateHandle) : ViewModel(), LifecycleObserver {

    init {
        Timber.e("init")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(){
        savedStateHandle.set("test", "Charles")
        Timber.e("onPause")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
        Timber.e("onResume : ${savedStateHandle.get<String>("test")}")
    }


    override fun onCleared() {
        super.onCleared()
        Timber.e("onCleared")
    }

}