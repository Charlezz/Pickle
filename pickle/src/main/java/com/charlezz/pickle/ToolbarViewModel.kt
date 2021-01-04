package com.charlezz.pickle

import androidx.lifecycle.MutableLiveData

class ToolbarViewModel {
    var visible = MutableLiveData(true)

    var title = MutableLiveData<CharSequence?>("Pickle")

    var subtitle = MutableLiveData<CharSequence>()

    var subtitleVisible = MutableLiveData(true)

    var marginTop = MutableLiveData(0)

    var marginIncludeInsets = MutableLiveData(true)

}
