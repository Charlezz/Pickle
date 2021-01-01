package com.charlezz.picklesample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.charlezz.pickle.data.entity.Media
import com.charlezz.picklesample.databinding.ItemViewBinding

class MainAdapter : RecyclerView.Adapter<MainViewHolder>() {
    private val items = ArrayList<Media>()

    fun setImages(mediaList: List<Media>) {
        items.clear()
        items.addAll(mediaList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MainViewHolder(ItemViewBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.setImage(items[position])
    }

    override fun getItemCount(): Int = items.size


}