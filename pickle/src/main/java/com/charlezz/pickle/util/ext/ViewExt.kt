package com.charlezz.pickle.util.ext

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams

fun View.setMarginTop(value: Int) = updateLayoutParams<ViewGroup.MarginLayoutParams> {
    topMargin = value
}

fun View.setMarginBottom(value: Int) = updateLayoutParams<ViewGroup.MarginLayoutParams> {
    bottomMargin = value
}

fun View.setMarginLeft(value: Int) = updateLayoutParams<ViewGroup.MarginLayoutParams> {
    leftMargin = value
}

fun View.setMarginRight(value: Int) = updateLayoutParams<ViewGroup.MarginLayoutParams> {
    rightMargin = value
}
