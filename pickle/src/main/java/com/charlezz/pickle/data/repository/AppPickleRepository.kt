package com.charlezz.pickle.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.charlezz.pickle.PickleConstants
import com.charlezz.pickle.data.entity.Media
import kotlinx.coroutines.flow.Flow
import timber.log.Timber


class AppPickleRepository constructor(
    private val context: Context
) : PickleRepository {

    private var currentPagingSource: PicklePagingSource? = null

    init {
        Timber.i("AppPickleRepository")
    }

    override fun getItems(
        selectionType: PicklePagingSource.SelectionType,
        bucketId: Int?,
        startPosition: Int,
        pageSize: Int
    ): Flow<PagingData<Media>>{
        Timber.i("getItems# selectionType = $selectionType, bucketId = $bucketId, startPosition = $startPosition, pageSize = $pageSize")
        return Pager(
            PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = true,
                initialLoadSize = PickleConstants.DEFAULT_PAGE_SIZE,
            ),
            initialKey = startPosition,
        ) {
            PicklePagingSource(
                context = context,
                bucketId = bucketId,
                selectionType = selectionType
            ).also {
                currentPagingSource = it
            }
        }.flow
    }

    override fun invalidate(){
        currentPagingSource?.invalidate()
    }

    override fun close(){
    }

}