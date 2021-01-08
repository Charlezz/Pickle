package com.charlezz.pickle.util.bindingadapter

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter

@BindingAdapter("toolbar_title")
fun setTitle(toolbar: Toolbar, title: CharSequence?) {
    if (toolbar.context is Activity) {
//        toolbar.title = title
        (toolbar.context as Activity).title = title
    }

}

@BindingAdapter("toolbar_subtitle", "toolbar_subtitle_visible", requireAll = false)
fun setSubtitle(toolbar: Toolbar, subtitle: CharSequence?, visible: Boolean?) {
    if (visible == true) {
        toolbar.subtitle = subtitle
    } else {
        toolbar.subtitle = null
    }
}

@BindingAdapter("toolbar_onTitleClick")
fun setOnToolbarTitleClickListener(toolbar: Toolbar, listener: View.OnClickListener?) {
    val tv = getTextView(toolbar, "mTitleTextView")
    toolbar.setOnClickListener(listener)
}

private fun getTextView(toolbar: Toolbar, fieldName: String): TextView? {
    try {
        val field = Toolbar::class.java.getDeclaredField(fieldName)
        field.isAccessible = true
        return field[toolbar] as TextView
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}