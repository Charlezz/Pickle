package com.charlezz.pickle.util

import android.content.Context

object PickleConstants {
    const val THUMBNAIL_TRANSITION_NAME = "thumbnail"
    const val DEFAULT_SPAN_COUNT = 3
    const val DEFAULT_PAGE_SIZE = 30
    const val DEFAULT_POSITION = 0
    const val NO_POSITION = -1
    const val KEY_CONFIG = "config"

    fun getAuthority(context: Context) = "${context.packageName}.pickle.fileprovider"
}