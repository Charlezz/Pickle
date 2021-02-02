package com.charlezz.pickle

import android.os.Environment
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class Config internal constructor(
    internal var singleMode:Boolean,
    var debugMode: Boolean = true,
    var environmentDir: String = Environment.DIRECTORY_DCIM,
    var dirToSave: String = "Camera"
) : Parcelable{
    companion object{
        val default = Config(false)
    }
}