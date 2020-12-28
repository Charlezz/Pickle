package com.charlezz.pickle.util.ext

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("visible")
fun setVisible(view : View, visible:Boolean?){
    view.visibility = if(visible==true) View.VISIBLE else View.GONE
}
