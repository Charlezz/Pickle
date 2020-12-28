package com.charlezz.pickle

import com.charlezz.pickle.data.entity.CameraItem
import com.charlezz.pickle.util.recyclerview.DataBindingAdapter
import javax.inject.Inject

class PickleHeaderAdapter
@Inject constructor(listener: CameraItem.OnItemClickListener) : DataBindingAdapter<CameraItem>() {
    init {
        items.add(CameraItem(listener))
    }
}