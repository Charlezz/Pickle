package com.charlezz.pickle

import android.os.Environment
import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
open class Config internal constructor(
    internal var singleMode:Boolean,
    var debugMode: Boolean = true,
    var environmentDir: String = Environment.DIRECTORY_DCIM,
    var dirToSave: String = "Camera",
    @StringRes var recentTextRes:Int = R.string.pickle_recent,
    @StringRes var doneTextRes:Int = R.string.pickle_done,
    @StringRes var okTextRes:Int = R.string.pickle_ok,
    @StringRes var confirmTextRes:Int = R.string.pickle_confirm,

    @StringRes var cancelTextRes:Int = R.string.pickle_cancel,
    @StringRes var albumsTextRes:Int = R.string.pickle_albums,

    ) : Parcelable{
    companion object{
        @JvmStatic
        val default = Config(false)
    }
}