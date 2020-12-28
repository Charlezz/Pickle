package com.charlezz.pickle

import androidx.recyclerview.widget.ConcatAdapter
import javax.inject.Inject

class PickleAdapters @Inject constructor(
    headerAdapter: PickleHeaderAdapter,
    val itemAdapter: PickleItemAdapter
) {
    val concatAdapter = ConcatAdapter(headerAdapter,itemAdapter)
//val concatAdapter = ConcatAdapter(itemAdapter)
}