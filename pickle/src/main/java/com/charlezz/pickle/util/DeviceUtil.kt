package com.charlezz.pickle.util

import android.os.Build

object DeviceUtil {

    fun isAndroid10Later() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    fun isAndroid9Earlier() = Build.VERSION.SDK_INT <= Build.VERSION_CODES.P
}