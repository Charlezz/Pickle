package com.charlezz.pickle

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.charlezz.pickle.data.entity.Media
import com.charlezz.pickle.util.PickleActivityContract
import com.charlezz.pickle.util.PickleActivitySingleContract


fun ComponentActivity.getPickleForSingle(callback:ActivityResultCallback<Media?>): ActivityResultLauncher<SingleConfig> {
    return this.registerForActivityResult(PickleActivitySingleContract(), callback)
}

fun Fragment.getPickleForSingle(callback:ActivityResultCallback<Media?>): ActivityResultLauncher<SingleConfig> {
    return this.registerForActivityResult(PickleActivitySingleContract(), callback)
}

fun ComponentActivity.getPickle(callback: ActivityResultCallback<ArrayList<Media>>): ActivityResultLauncher<Config> {
    return this.registerForActivityResult(PickleActivityContract(), callback)
}

fun Fragment.getPickle(callback: ActivityResultCallback<ArrayList<Media>>): ActivityResultLauncher<Config> {
    return this.registerForActivityResult(PickleActivityContract(), callback)
}