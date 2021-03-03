package com.charlezz.pickle.fragments.main

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.charlezz.pickle.data.entity.Media
import kotlinx.coroutines.flow.Flow

interface PickleRepository {

    fun getItems(
        selectionType: PicklePagingSource.SelectionType,
        bucketId: Long?,
        startPosition: Int,
        pageSize: Int
    ): Flow<PagingData<Media>>

    fun invalidate()

    fun getCount(): LiveData<Int>
}