package com.charlezz.pickle.util.bindingadapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.Surface
import android.view.View
import androidx.core.view.ViewCompat
import androidx.databinding.BindingAdapter
import com.charlezz.pickle.util.DeviceUtil
import com.charlezz.pickle.util.ext.setMarginBottom
import com.charlezz.pickle.util.ext.setMarginLeft
import com.charlezz.pickle.util.ext.setMarginRight
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
    ViewCompat.setOnApplyWindowInsetsListener(view) { view, insets ->
        if (include) {
            view.setMarginTop(insets.systemWindowInsetTop + marginTop)
            view.setMarginBottom(insets.systemWindowInsetBottom + marginBottom)
        } else {
            view.setMarginTop(marginTop)
            view.setMarginBottom(marginBottom)
        }

        if(DeviceUtil.isAndroid5Later()){
            when (view.display.rotation) {
                Surface.ROTATION_90 -> {
                    view.setMarginLeft(0)
                    view.setMarginRight(insets.systemWindowInsetRight)
                }
                Surface.ROTATION_270 -> {
                    view.setMarginLeft(insets.systemWindowInsetLeft)
                    view.setMarginRight(0)
                }
                else -> {
                    view.setMarginRight(insets.systemWindowInsetRight)
                    view.setMarginLeft(insets.systemWindowInsetLeft)
                }
            }
        }
        insets
    }


}


@BindingAdapter(value = ["crossfadeVisible"])
fun crossfade(view: View, visible: Boolean) {
    if (visible) {
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setListener(null)
    } else {
        view.animate()
            .alpha(0f)
            .setListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    view.visibility = View.GONE
                }
            })
    }
}

@BindingAdapter(value = ["crossfadeVisibleNoBlinking"])
fun crossfadeNoBlinking(view: View, visible: Boolean) {
    if (visible) {
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setListener(null)
    } else {
        view.visibility = View.GONE
        view.animate()
            .alpha(0f)
            .setListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    view.visibility = View.GONE
                }
            })
    }

}