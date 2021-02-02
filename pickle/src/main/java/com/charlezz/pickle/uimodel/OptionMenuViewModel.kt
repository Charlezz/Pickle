package com.charlezz.pickle.uimodel

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.charlezz.pickle.BR
import com.charlezz.pickle.R
import com.charlezz.pickle.databinding.ViewPickleOptionBinding
import com.charlezz.pickle.util.lifecycle.SingleLiveEvent
import javax.inject.Inject

class OptionMenuViewModel @Inject constructor(
    val context: Context,
) : BaseObservable() {

    val clickEvent = SingleLiveEvent<Unit>(300)

    val binding: ViewPickleOptionBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.view_pickle_option,
        null,
        false
    )

    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        binding.lifecycleOwner = lifecycleOwner
    }

    fun onClick() {
        clickEvent.call()
    }

    var isEnabled: Boolean = true
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.enabled)
            notifyPropertyChanged(BR.titleTextColor)
        }

    var selectedCountString: String = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.selectedCountString)
        }


    var selectedCountTextColor: Int = Color.parseColor("#ffffff")
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.selectedCountTextColor)
        }

    var selectedCountVisible: Boolean = true
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.selectedCountVisible)
        }

    var selectedCountBackgroundRes: Drawable? = ContextCompat.getDrawable(context, R.drawable.bg_media_picker_option_count)
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.selectedCountBackgroundRes)
        }

    var menuTitle: String = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.menuTitle)
        }

    private var titleTextColor: Int = Color.parseColor("#ffffff")
    private var titleDisabledTextColor: Int = Color.parseColor("#aaaaaa")

    fun setTitleTextColor(color: Int) {
        titleTextColor = color
        notifyPropertyChanged(BR.titleTextColor)
    }

    fun setDisableTitleTextColor(color: Int) {
        titleDisabledTextColor = color
        notifyPropertyChanged(BR.titleTextColor)
    }

    @Bindable
    fun getTitleTextColor(): Int = if (isEnabled) titleTextColor else titleDisabledTextColor

    var menuTitleVisible: Boolean = true
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.menuTitleVisible)
        }


    fun onCreateOptionMenu(menu: Menu) {
        binding.viewModel = this
        val menuItem = menu.add(0, 0, 0, "")
        menuItem.actionView = binding.root
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        menuItem.isEnabled = isEnabled
        notifyChange()
    }
}