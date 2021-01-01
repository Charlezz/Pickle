package com.charlezz.pickle.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.charlezz.pickle.data.entity.Media
import com.charlezz.pickle.util.PickleConstants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import timber.log.Timber


class AppPickleRepository constructor(
    private val context: Context
) : PickleRepository {

    private var currentPagingSource: PicklePagingSource? = null

    @ExperimentalCoroutinesApi
    private val countChannel = ConflatedBroadcastChannel<Int?>()

    init {
        Timber.d("AppPickleRepository")
    }
    override fun getItems(
        selectionType: PicklePagingSource.SelectionType,
        bucketId: Int?,
        startPosition: Int,
        pageSize: Int
    ): Flow<PagingData<Media>>{
        Timber.d("getItems# selectionType = $selectionType, bucketId = $bucketId, startPosition = $startPosition, pageSize = $pageSize")
        return Pager(
            PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = true,
                initialLoadSize = PickleConstants.DEFAULT_PAGE_SIZE
            ),
            initialKey = startPosition,
        ) {

            PicklePagingSource(
                context = context,
                bucketId = bucketId,
                selectionType = selectionType,
                countChannel = countChannel
            ).also {
                currentPagingSource = it
            }
        }.flow
    }

    override fun invalidate(){
        currentPagingSource?.invalidate()
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun getCount(): Flow<Int?> = countChannel.asFlow()

}