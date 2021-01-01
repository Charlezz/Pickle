package com.charlezz.pickle.fragments.detail

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.bumptech.glide.Glide
import com.charlezz.pickle.R
import com.charlezz.pickle.Selection
import com.charlezz.pickle.data.MediaItemDiffCallback
import com.charlezz.pickle.data.entity.MediaItem
import com.charlezz.pickle.databinding.ViewPickleMediaDetailBinding
import com.charlezz.pickle.util.recyclerview.DataBindingHolder
import timber.log.Timber
import javax.inject.Inject

class PickleDetailAdapter @Inject constructor(
    diffCallback: MediaItemDiffCallback
) : PagingDataAdapter<MediaItem, DataBindingHolder<ViewPickleMediaDetailBinding>>(diffCallback = diffCallback) {

    var selection: Selection? = null

    override fun getItemViewType(position: Int): Int {
        return R.layout.view_pickle_media_detail
    }

    override fun onBindViewHolder(
        holder: DataBindingHolder<ViewPickleMediaDetailBinding>,
        position: Int
    ) {
        val item = getItem(position)
        item?.let { item ->
            Timber.d("uri = ${item.getUri()}")
            holder.binding.item = item
            holder.binding.selection = selection
            holder.binding.position = position
            Glide.with(holder.binding.photoView).load(item.getUri()).into(holder.binding.photoView)
            holder.binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DataBindingHolder<ViewPickleMediaDetailBinding> {
        return DataBindingHolder(parent, viewType)
    }

}