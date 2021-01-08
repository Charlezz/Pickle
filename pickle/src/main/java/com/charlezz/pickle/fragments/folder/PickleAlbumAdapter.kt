package com.charlezz.pickle.fragments.folder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.charlezz.pickle.R
import com.charlezz.pickle.data.entity.AlbumItem
import com.charlezz.pickle.databinding.ViewPickleAlbumBinding
import com.charlezz.pickle.util.MeasureUtil
import com.charlezz.pickle.util.glide.RoundedCornersLineOverDrawTransFormation
import com.charlezz.pickle.util.recyclerview.DataBindingAdapter
import com.charlezz.pickle.util.recyclerview.DataBindingHolder
import javax.inject.Inject


class PickleAlbumAdapter @Inject constructor(
    context: Context
) : DataBindingAdapter<AlbumItem, ViewPickleAlbumBinding>() {

    private val folderRadius =
        MeasureUtil.getDimension(context, resId = R.dimen.pickle_folder_corner_radius)
    private val outlineWidth =
        MeasureUtil.getDimension(context, resId = R.dimen.pickle_folder_outline_width)
    private val outlineColor = ContextCompat.getColor(context, R.color.light_white)

    fun submitItems(newItems: List<AlbumItem>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = items.size

            override fun getNewListSize(): Int = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition].album.bucketId == newItems[newItemPosition].album.bucketId
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition].album == newItems[newItemPosition].album
            }
        })
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(
        holder: DataBindingHolder<ViewPickleAlbumBinding>,
        position: Int
    ) {
        val item: AlbumItem = items[position]
        Glide.with(holder.binding.image)
            .load(item.getRecentMediaUri())
//            .transform(CenterCrop(), RoundedCorners(folderRadius))
            .transform(
                CenterCrop(),
                RoundedCornersLineOverDrawTransFormation(
                    folderRadius.toInt(),
                    outlineWidth,
                    outlineColor
                )
            )
            .into(holder.binding.image)
        super.onBindViewHolder(holder, position)
    }

}