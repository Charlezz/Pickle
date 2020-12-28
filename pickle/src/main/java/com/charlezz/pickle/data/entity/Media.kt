package com.charlezz.pickle.data.entity

import android.content.ContentUris
import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Media(
    val contentUri: Uri,
    val id: Long,
    val relativePath: String?,
    val mediaType: Int,
    val mimeType: String,
    val dateModified: Long?,
    val dateTaken: Long?,
    val dateAdded: Long?,
    val width: Int?,
    val height: Int?,
    val orientation: Int?,
    val size: Int,
    val duration: Long?
) : Parcelable

fun Media.getUri(): Uri {
    return ContentUris.withAppendedId(contentUri, id)
}