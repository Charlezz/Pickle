package com.charlezz.pickle.util.bindingadapter

import android.view.View
import androidx.core.view.ViewCompat
import androidx.databinding.BindingAdapter
import com.charlezz.pickle.util.ext.setMarginBottom
import com.charlezz.pickle.util.ext.setMarginTop

@BindingAdapter(value = ["layout_width", "layout_height"], requireAll = false)
fun setLayoutParams(view: View, width: Int?, height: Int?) {
    val layoutParams = view.layoutParams
    width?.let {
        layoutParams.width = it
    }
    height?.let {
        layoutParams.height = it
    }
    view.layoutParams = layoutParams
}

@BindingAdapter(value = ["visible"])
fun setVisible(view: View, visible: Boolean?) {
    view.visibility = if (visible == true) View.VISIBLE else View.GONE
}

@BindingAdapter(
    "window_marginTop",
    "window_marginBottom",
    "window_includeInsets",
    requireAll = false
)
fun setMargin(
    view: View,
    marginTop: Int,
    marginBottom:Int,
    include: Boolean
) {
    ViewCompat.setOnApplyWindowInsetsListener(view) { view, insets ->
        if (include) {
            view.setMarginTop(insets.systemWindowInsetTop + marginTop)
            view.setMarginBottom(insets.systemWindowInsetBottom+marginBottom)
        } else {
            view.setMarginTop(marginTop)
            view.setMarginBottom(marginBottom)
        }
        insets
    }

}