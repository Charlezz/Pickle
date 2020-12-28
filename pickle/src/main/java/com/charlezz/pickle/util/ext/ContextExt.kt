package com.charlezz.pickle.util.ext

import android.content.Context

fun Context.showToast(message:String){
    showToast(message)
}

fun Context.showToast(resId:Int){
    showToast(getString(resId))
}