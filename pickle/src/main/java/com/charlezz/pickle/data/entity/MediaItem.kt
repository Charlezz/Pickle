package com.charlezz.pickle.data.entity

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import androidx.databinding.BaseObservable
import com.charlezz.pickle.Config

class MediaItem constructor(
    val media: Media,
    val config:Config,
    val listener: OnItemClickListener
) : BaseObservable() {

    fun isCheckViewVisible():Boolean{
        return !config.singleMode
    }

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
        fun onItemClick(view: View, item: MediaItem, position:Int)

        fun onCheckBoxClick(item: MediaItem)
    }

}