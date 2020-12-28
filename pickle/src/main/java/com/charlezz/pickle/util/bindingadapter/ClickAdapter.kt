package com.charlezz.pickle.util.bindingadapter

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.charlezz.pickle.util.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class ClickAdapter(val lifecycle: Lifecycle) : LifecycleObserver {
    private val logger = Logger.getLogger(ClickAdapter::class.java.simpleName)

    private var disposable: Disposable? = null
    private val publishSubject = PublishSubject.create<Pair<View, View.OnClickListener?>>()
    private val TIME_OUT = 200L

    init {
        lifecycle.addObserver(this)
    }

    @BindingAdapter(value = ["onClick"])
    fun setOnClickListener(view: View, listener: View.OnClickListener?) {
        clear()
        view.setOnClickListener {
            publishSubject.onNext(Pair(it, listener))
        }
        disposable = publishSubject
            .throttleFirst(TIME_OUT, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .subscribe { it?.second?.onClick(it.first) }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        clear()
    }

    private fun clear() {
        disposable?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
    }


}