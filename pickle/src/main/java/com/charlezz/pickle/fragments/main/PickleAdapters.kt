package com.charlezz.pickle.fragments.main

import androidx.recyclerview.widget.ConcatAdapter
import javax.inject.Inject

class PickleAdapters @Inject constructor(
    val headerAdapter: PickleHeaderAdapter,
    val itemAdapter: PickleItemAdapter
) {
    val concatedAdapter = ConcatAdapter(headerAdapter,itemAdapter)


    fun getHeaderItemCount():Int = headerAdapter.itemCount

}