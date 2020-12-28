package com.charlezz.picklesample

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.charlezz.pickle.data.entity.Media
import com.charlezz.pickle.data.entity.getUri
import com.charlezz.picklesample.databinding.ItemViewBinding

class MainViewHolder(private val binding: ItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

    fun setImage(media: Media) {
        Glide.with(binding.imageView).load(media.getUri()).into(binding.imageView)

        if (binding.imageView.parent is ConstraintLayout) {
            val layoutParams = binding.imageView.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.dimensionRatio = if (media.orientation == 0 || media.orientation == 180) {
                "${media.width}:${media.height}"
            } else {
                "${media.height}:${media.width}"
            }
            binding.imageView.layoutParams = layoutParams
        }

        binding.executePendingBindings()
    }

}