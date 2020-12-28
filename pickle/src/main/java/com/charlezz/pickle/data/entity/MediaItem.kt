package com.charlezz.pickle.data.entity

import android.content.ContentUris
import android.net.Uri
import androidx.databinding.BaseObservable

class MediaItem constructor(
    val media: Media,
    val listener: OnItemClickListener
) : BaseObservable() {

    fun getUri(): Uri {
        return ContentUris.withAppendedId(media.contentUri, media.id)
    }

    fun getId() = media.id

    interface OnItemClickListener {
        fun onItemClick(item: MediaItem)
    }

}