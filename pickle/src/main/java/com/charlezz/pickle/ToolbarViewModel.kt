package com.charlezz.pickle

import androidx.databinding.BaseObservable
import androidx.lifecycle.MutableLiveData
import com.charlezz.pickle.util.lifecycle.SingleLiveEvent

class ToolbarViewModel : BaseObservable() {
    val visible = MutableLiveData(true)

    val title = MutableLiveData<CharSequence?>("Pickle")

    val subtitle = MutableLiveData<CharSequence>()

    val subtitleVisible = MutableLiveData(true)

    val marginTop = MutableLiveData(0)

    val marginIncludeInsets = MutableLiveData(true)

    var titleClickEvent = SingleLiveEvent<Unit>(interval = 300)

    fun onTitleClick() {
        titleClickEvent.call()
    }
}
