package com.charlezz.pickle.util.bindingadapter

import android.app.Activity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter

@BindingAdapter("toolbar_title")
fun setTitle(toolbar: Toolbar, title: CharSequence?) {
    if(toolbar.context is Activity){
//        toolbar.title = title
        (toolbar.context as Activity).title = title
    }

}

@BindingAdapter("toolbar_subtitle","toolbar_subtitle_visible", requireAll = false)
fun setSubtitle(toolbar: Toolbar, subtitle: CharSequence?,visible: Boolean?) {
    if(visible==true){
        toolbar.subtitle = subtitle
    }else{
        toolbar.subtitle = null
    }
}
