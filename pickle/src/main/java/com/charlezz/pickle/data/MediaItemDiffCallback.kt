package com.charlezz.pickle.data

import androidx.recyclerview.widget.DiffUtil
import com.charlezz.pickle.data.entity.MediaItem
import javax.inject.Inject

class MediaItemDiffCallback @Inject constructor(

) : DiffUtil.ItemCallback<MediaItem>() {
    override fun areItemsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
        return oldItem.media.id == newItem.media.id
    }

    override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
        return oldItem.media == newItem.media
    }
}