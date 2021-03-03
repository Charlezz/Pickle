package com.charlezz.picklesample

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.charlezz.pickle.data.entity.Media
import com.charlezz.picklesample.databinding.ItemViewBinding

class MainViewHolder(private val binding: ItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
    fun setImage(media: Media) {
        Glide.with(binding.imageView).load(media.getUri()).into(binding.imageView)
        binding.executePendingBindings()
    }

}