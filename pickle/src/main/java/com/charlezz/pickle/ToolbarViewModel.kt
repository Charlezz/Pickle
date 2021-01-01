package com.charlezz.pickle

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class ToolbarViewModel : BaseObservable() {
    var visible: Boolean = true
    set(value){
        field = value
        notifyPropertyChanged(BR.visible)
    }
    @Bindable get

    var title: CharSequence? = "Pickle"
        set(value) {
            field = value
            notifyPropertyChanged(BR.title)
        }
        @Bindable get
    var subtitle: CharSequence? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.subtitle)
        }
        @Bindable get

    var subtitleVisible:Boolean = true
    set(value){
        field = value
        notifyPropertyChanged(BR.subtitleVisible)
    }
    @Bindable get
}
