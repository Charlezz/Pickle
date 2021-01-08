package com.charlezz.pickle.fragments.main

import com.charlezz.pickle.data.entity.CameraItem
import com.charlezz.pickle.databinding.ViewCameraItemBinding
import com.charlezz.pickle.util.recyclerview.DataBindingAdapter
import javax.inject.Inject

class PickleHeaderAdapter
@Inject constructor(listener: CameraItem.OnItemClickListener) : DataBindingAdapter<CameraItem, ViewCameraItemBinding>() {
    init {
        items.add(CameraItem(listener))
    }
}