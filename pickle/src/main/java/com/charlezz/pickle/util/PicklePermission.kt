package com.charlezz.pickle.util

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.subjects.AsyncSubject

class PicklePermission(val activity: AppCompatActivity):LifecycleObserver{
    private val logger = Logger.getLogger("PicklePermission")

    companion object{
        val REQ_CODE_PERMISSIONS = 0
        val PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private val disposables = CompositeDisposable()
    private lateinit var result:AsyncSubject<Boolean>


    fun request(): AsyncSubject<Boolean>{
        result = AsyncSubject.create()
        disposables.add(
            PERMISSIONS.toObservable()
                .filter {
                    ActivityCompat.checkSelfPermission(activity, it) !=
                            PackageManager.PERMISSION_GRANTED
                }
                .toList()
                .subscribe({
                    if(it.isNotEmpty()){
                        ActivityCompat.requestPermissions(activity, it.toTypedArray(),
                            REQ_CODE_PERMISSIONS
                        )
                    }else{
                        logger.i("complete")
                        result.onNext(true)
                        result.onComplete()
                    }
                }, { TODO("에러처리") })
        )
        return result
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode== REQ_CODE_PERMISSIONS){
            for(grantResult in grantResults){
                if(grantResult != PackageManager.PERMISSION_GRANTED){
                    result.onNext(false)
                    result.onComplete()
                    return
                }
            }
            result.onNext(true)
            result.onComplete()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(){
        if(!disposables.isDisposed){
            disposables.dispose()
        }
    }
}