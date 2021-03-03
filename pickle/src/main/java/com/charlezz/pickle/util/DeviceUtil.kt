package com.charlezz.pickle.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object DeviceUtil {

    fun isAndroid11Later() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    fun isAndroid10Later() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    fun isAndroid5Later() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

    fun hasPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

}