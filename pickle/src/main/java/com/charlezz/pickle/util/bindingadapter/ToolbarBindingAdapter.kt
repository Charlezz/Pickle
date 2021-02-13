package com.charlezz.pickle.util.bindingadapter

import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import com.charlezz.pickle.util.MeasureUtil

@BindingAdapter("toolbar_navigationIcon")
fun setNavigationIcon(toolbar: Toolbar, drawable: Drawable?) {
    toolbar.navigationIcon = drawable
}

@BindingAdapter("toolbar_navigationIconBackground")
fun setNavigationIconBackground(toolbar:Toolbar, drawable: Drawable?){
    findViewByFieldName<ImageButton>(toolbar, "mNavButtonView")
        ?.background = drawable
}

@BindingAdapter(value = ["toolbar_titleDrawableRight"])
fun setTitleDrawableRight(toolbar: Toolbar, @DrawableRes titleDrawableRigth: Int) {
    val titleTextView = getTitleView(toolbar)
    titleTextView?.let { tv ->
        tv.compoundDrawablePadding = MeasureUtil.dpToPx(toolbar.context, 6f)
        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, titleDrawableRigth, 0)
    }

}

@BindingAdapter("toolbar_titleTextColor")
fun setTitleTextColor(toolbar: Toolbar, @ColorInt color: Int) {
    toolbar.setTitleTextColor(color)
}

@BindingAdapter(value = ["toolbar_onTitleClickListener"])
fun setOnTitleClickListener(toolbar: Toolbar, onTitleClickListener: View.OnClickListener?) {
    val titleTextView: TextView? = getTitleView(toolbar)
    if (onTitleClickListener != null) {
        titleTextView?.setOnClickListener(onTitleClickListener)
    }
}

@BindingAdapter(value = ["toolbar_title"])
fun setTitle(toolbar: Toolbar, title: String?) {
    if (toolbar.context is AppCompatActivity) {
        (toolbar.context as AppCompatActivity).title = title
    } else {
        toolbar.title = title
    }

}

@BindingAdapter(value = ["toolbar_alignCenter"])
fun setAlignCenter(toolbar: Toolbar, isAlignCenter: Boolean) {
    if (isAlignCenter) {
        val titleTextView = getTitleView(toolbar)
        val subtitleTextView = getSubTitleView(toolbar)

        titleTextView?.gravity = Gravity.CENTER
        subtitleTextView?.gravity = Gravity.CENTER

        var layoutParams = titleTextView?.layoutParams as Toolbar.LayoutParams?
        layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT

        layoutParams = subtitleTextView?.layoutParams as Toolbar.LayoutParams?
        layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT

        toolbar.requestLayout()
    }
}

@BindingAdapter(value = ["toolbar_titleBackground"])
fun setTitleBackground(toolbar: Toolbar, background: Drawable?) {
    getTitleView(toolbar)?.background = background
}


private fun getTitleView(toolbar: Toolbar): TextView? {
    return findViewByFieldName(toolbar, "mTitleTextView")
}

private fun getSubTitleView(toolbar: Toolbar): TextView? {
    return findViewByFieldName(toolbar, "mSubtitleTextView")
}

private fun <T:View> findViewByFieldName(toolbar: Toolbar, fieldName: String): T? {
    try {
        val field = Toolbar::class.java.getDeclaredField(fieldName)
        field.isAccessible = true
        return field[toolbar] as T
    } catch (e: Exception) {
    }
    return null
}
