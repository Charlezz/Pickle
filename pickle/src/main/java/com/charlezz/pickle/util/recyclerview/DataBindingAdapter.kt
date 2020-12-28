package com.charlezz.pickle.util.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

open class DataBindingAdapter<DBA : DataBindingAware> constructor(
    var dataBindingComponent: DataBindingComponent? = null,
) : RecyclerView.Adapter<DataBindingHolder<ViewDataBinding>>() {

    val items: ArrayList<DBA> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DataBindingHolder<ViewDataBinding> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewDataBinding =
            DataBindingUtil.inflate<ViewDataBinding>(
                layoutInflater,
                viewType,
                parent,
                false,
                dataBindingComponent
            )
        return DataBindingHolder(viewDataBinding)
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].getLayoutResId()
    }

    override fun onBindViewHolder(holder: DataBindingHolder<ViewDataBinding>, position: Int) {
        val item = items[position]
        holder.binding.setVariable(item.getBindingResId(), item)
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = items.size
}