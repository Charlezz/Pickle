package com.charlezz.pickle.data

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.lifecycle.*
import timber.log.Timber
import javax.inject.Inject

class PickleContentObserver @Inject constructor(
    val context: Context
) : ContentObserver(Handler(Looper.getMainLooper())), LifecycleObserver {

    private val contentChangedEvent = MutableLiveData<Uri?>()


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun register(){
        Timber.i("register()")
        context.contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            this
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun unregister(){
        Timber.i("unregister()")
        context.contentResolver.unregisterContentObserver(this)
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        Timber.i("onChange : selfChange = $selfChange uri = $uri onMainThread = ${Thread.currentThread() == Looper.getMainLooper().thread}")
        contentChangedEvent.value = uri

    }

    fun getContentChangedEvent(): LiveData<Uri?> {
        return contentChangedEvent
    }
}