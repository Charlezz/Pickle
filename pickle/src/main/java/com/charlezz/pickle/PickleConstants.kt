package com.charlezz.pickle

import android.content.Context

object PickleConstants {
    const val DEFAULT_SPAN_COUNT = 3
    const val DEFAULT_PAGE_SIZE = 30
    const val DEFAULT_POSITION = 0

    fun getAuthority(context: Context) = "${context.packageName}.pickle.fileprovider"
}