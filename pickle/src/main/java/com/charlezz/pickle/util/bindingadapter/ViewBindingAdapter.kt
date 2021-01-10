package com.charlezz.pickle.util.bindingadapter

import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.databinding.BindingAdapter
import com.charlezz.pickle.util.DeviceUtil
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
    marginBottom: Int,
    include: Boolean
) {

    if (DeviceUtil.isAndroid11Later()) {
        view.setOnApplyWindowInsetsListener { v, insets ->
            val insets = view.rootWindowInsets.getInsets(WindowInsets.Type.systemBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                if (include) {
                    updateMargins(
                        insets.left,
                        insets.top + marginTop,
                        insets.right,
                        insets.bottom + marginBottom
                    )
                } else {
                    updateMargins(
                        insets.left,
                        marginTop,
                        insets.right,
                        marginBottom
                    )
                }
            }
            WindowInsets.Builder().setInsets(WindowInsets.Type.systemBars(), insets).build()
        }
    } else {
        ViewCompat.setOnApplyWindowInsetsListener(view) { view, insets ->
            if (include) {
                view.setMarginTop(insets.systemWindowInsetTop + marginTop)
                view.setMarginBottom(insets.systemWindowInsetBottom + marginBottom)
            } else {
                view.setMarginTop(marginTop)
                view.setMarginBottom(marginBottom)
            }
            insets
        }
    }


}