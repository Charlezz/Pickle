package com.charlezz.pickle.data.entity

import com.charlezz.pickle.BR
import com.charlezz.pickle.R
import com.charlezz.pickle.util.recyclerview.DataBindingAware

class CameraItem constructor(val listener: OnItemClickListener) : DataBindingAware {
    override fun getLayoutResId() = R.layout.view_camera_item

    override fun getBindingResId() = BR.item

    interface OnItemClickListener{
        fun onCameraClick()
    }
}