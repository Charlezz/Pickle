package com.charlezz.pickle.data.repository

import androidx.paging.PagingData
import com.charlezz.pickle.data.entity.Media
import kotlinx.coroutines.flow.Flow

interface PickleRepository {

    fun getItems(
        selectionType:PicklePagingSource.SelectionType,
        bucketId: Int?,
        startPosition: Int,
        pageSize: Int
    ): Flow<PagingData<Media>>

    fun invalidate()

    fun getCount():Flow<Int?>
}