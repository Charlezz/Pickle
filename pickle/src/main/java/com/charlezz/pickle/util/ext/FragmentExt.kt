package com.charlezz.pickle.util.ext

import androidx.fragment.app.Fragment

fun Fragment.showToast(message:String){
    activity?.showToast(message)
}

fun Fragment.showToast(resId:Int){
    activity?.showToast(getString(resId))
}