package com.charlezz.pickle.util.lifecycle

import android.os.SystemClock
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean


class SingleLiveEvent<T> constructor(private val interval: Long = NO_INTERVAL) : MutableLiveData<T?>() {

    companion object {
        const val NO_INTERVAL = 0L
    }

    private var lastClickTime: Long = 0
    private val mPending = AtomicBoolean(false)

    fun invalidate():SingleLiveEvent<T>{
        mPending.set(false)
        return this
    }
    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T?>) {
        super.observe(owner, { t ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    override fun setValue(t: T?) {
        if (interval != NO_INTERVAL) {
            val currentClickTime = SystemClock.uptimeMillis()
            val elapsedTime: Long = currentClickTime - lastClickTime
            lastClickTime = currentClickTime

            if (elapsedTime <= interval) {
                return
            }
        }
        mPending.set(true)
        super.setValue(t)
    }

    @MainThread
    fun call() {
        value = null
    }

}