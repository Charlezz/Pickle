package com.charlezz.pickle.data.entity

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.databinding.BaseObservable

class MediaItem constructor(
    val media: Media,
    val listener: OnItemClickListener
) : BaseObservable() {

    fun getUri(): Uri {
        return ContentUris.withAppendedId(media.contentUri, media.id)
    }

    fun isVideo():Boolean{
        return media.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
    }

    fun isImage():Boolean{
        return media.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
    }

    fun getId() = media.id

    interface OnItemClickListener {
        fun onItemClick(item: MediaItem, position:Int)

        fun onCheckBoxClick(item: MediaItem)
    }

}