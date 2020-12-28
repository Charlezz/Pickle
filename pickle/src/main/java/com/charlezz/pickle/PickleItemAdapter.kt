package com.charlezz.pickle

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.charlezz.pickle.data.entity.Media
import com.charlezz.pickle.data.entity.MediaItem
import com.charlezz.pickle.databinding.ViewPickleMediaBinding
import com.charlezz.pickle.util.recyclerview.DataBindingHolder
import javax.inject.Inject

class PickleItemAdapter @Inject constructor() :
    PagingDataAdapter<MediaItem, DataBindingHolder<ViewPickleMediaBinding>>(diffCallback) {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<MediaItem>() {
            override fun areItemsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
                return oldItem.media.id == newItem.media.id
            }

            override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
                return oldItem.media == newItem.media
            }
        }
    }

    var selection:Selection<Media>? = null

    override fun getItemViewType(position: Int): Int {
        return R.layout.view_pickle_media
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DataBindingHolder<ViewPickleMediaBinding> {
        return DataBindingHolder(parent, viewType)
    }


    override fun onBindViewHolder(holder: DataBindingHolder<ViewPickleMediaBinding>, position: Int) {
        val item = getItem(position)
        item?.let { item ->
            holder.binding.item = item
            holder.binding.selection = selection
            Glide.with(holder.binding.image).load(item.getUri()).into(holder.binding.image)
            holder.binding.executePendingBindings()
        }
    }

}