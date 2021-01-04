package com.charlezz.pickle.fragments.main

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.charlezz.pickle.R
import com.charlezz.pickle.Selection
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

    var selection: Selection? = null

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
            holder.binding.position = position
            Glide.with(holder.binding.image)
                .load(item.getUri())
                .listener(object:RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        onImageListener?.onLoaded(position)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        onImageListener?.onLoaded(position)
                        return false
                    }
                })
                .into(holder.binding.image)
            ViewCompat.setTransitionName(holder.binding.image, item.getUri().toString())
            holder.binding.executePendingBindings()
        }
    }

    var onImageListener:OnImageListener? = null
    interface OnImageListener{
        fun onLoaded(position:Int)
    }
}