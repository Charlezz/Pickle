package com.charlezz.pickle.util.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

open class DataBindingHolder<VDB : ViewDataBinding> : RecyclerView.ViewHolder {
    val binding: VDB

    constructor(binding: VDB) : super(binding.root) {
        this.binding = binding
    }

    constructor(parent: ViewGroup, layoutResId: Int) : this(
        DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutResId, parent, false)
    )

}
