package com.charlezz.pickle.data.entity

import android.net.Uri
import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import kotlinx.parcelize.Parcelize

@Parcelize
data class Album constructor(
    val contentUri: Uri,
    val recentMediaId: Long,
    val bucketId: Long?,
    val name: String,
    var count: Int,
    val order:Long,
    @RequiresApi(Build.VERSION_CODES.Q)
    val relativePath:String?,
    val data:String?
) : Parcelable {

    fun increaseCount() {
        count += 1
    }

}