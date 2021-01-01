package com.charlezz.pickle

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Config(
    var title: CharSequence? = null
):Parcelable {
    companion object {
        val default = Config()
    }


}