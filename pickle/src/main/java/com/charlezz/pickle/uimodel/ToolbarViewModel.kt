package com.charlezz.pickle.uimodel

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.charlezz.pickle.BR
import com.charlezz.pickle.R

class ToolbarViewModel(
    val context: Context,
) : BaseObservable() {

    @DrawableRes
    var titleDrawableRight: Int = 0
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.titleDrawableRight)
        }

    var navigationIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ico_pickle_close)
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.navigationIcon)
        }

    var navigationIconBackground: Drawable? = ContextCompat.getDrawable(context, R.drawable.bg_pickle_ripple)
        @Bindable get
        set(value){
            field = value
            notifyPropertyChanged(BR.navigationIconBackground)
        }

    @ColorInt
    var backgroundColorRes: Int = R.color.black
        set(value) {
            field = value
            notifyPropertyChanged(BR.backgroundColor)
        }

    @ColorInt
    var titleTextColorRes: Int = R.color.white
        set(value) {
            field = value
            notifyPropertyChanged(BR.titleTextColor)
        }

    var title: String = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.title)
            notifyChange()
        }

    var onTitleClickListener: View.OnClickListener? = null
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.onTitleClickListener)
        }

    var alignCenter = false


    @Bindable
    fun getBackgroundColor(): Int {
        return ContextCompat.getColor(context, backgroundColorRes)
    }

    @Bindable
    fun getTitleTextColor(): Int = ContextCompat.getColor(context, titleTextColorRes)

    @Bindable
    fun getTitleBackground(): Drawable? = ContextCompat.getDrawable(context, R.drawable.bg_pickle_ripple)

}

