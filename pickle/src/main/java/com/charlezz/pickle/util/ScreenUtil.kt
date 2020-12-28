package com.charlezz.pickle.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.DimenRes
import kotlin.math.max

object ScreenUtil {
    fun getSpanCount(context: Context, columnWidthPx: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return displayMetrics.let {
            val widthPixels = it.widthPixels.toFloat()
            return (widthPixels / columnWidthPx).toInt()
        }
    }

    fun getProperSpanCount(context: Context, @DimenRes columnWidthDimen: Int, minSpan: Int = 1): Int {
        val columnWidthPx = context.resources.getDimensionPixelSize(columnWidthDimen)
        return max(getSpanCount(context, columnWidthPx), minSpan)
    }

    fun dpToPx(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}

