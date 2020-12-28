package com.charlezz.pickle.util.bindingadapter

import android.graphics.PorterDuff
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("tint")
fun setTint(view: ImageView, color: Int) {
    if (color == 0) {
        view.clearColorFilter()
    } else {
        view.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
}