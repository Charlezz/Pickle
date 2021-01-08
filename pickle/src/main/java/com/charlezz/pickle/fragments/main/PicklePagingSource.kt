package com.charlezz.pickle.fragments.main

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.charlezz.pickle.data.entity.Media
import com.charlezz.pickle.util.DeviceUtil
import com.charlezz.pickle.util.PickleConstants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import timber.log.Timber
import kotlin.math.max

@SuppressLint("RequiresApi", "InlinedApi")
@OptIn(ExperimentalCoroutinesApi::class)
class PicklePagingSource(
    context: Context,
    bucketId: Long? = null,
    selectionType: SelectionType,
    countChannel: ConflatedBroadcastChannel<Int?>
) : PagingSource<Int, Media>() {


    private val cursor: Cursor?

    private val contentUri = PickleConstants.getContentUri()

    private val projection = arrayListOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
        MediaStore.Files.FileColumns.MIME_TYPE,
        MediaStore.Files.FileColumns.DATE_MODIFIED,
        MediaStore.Files.FileColumns.DATE_TAKEN,
        MediaStore.Files.FileColumns.DATE_ADDED,
        MediaStore.Files.FileColumns.WIDTH,
        MediaStore.Files.FileColumns.HEIGHT,
        MediaStore.Files.FileColumns.ORIENTATION,
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Files.FileColumns.DURATION
    ).apply {
        if (DeviceUtil.isAndroid10Later()) {
            this.add(MediaStore.Files.FileColumns.RELATIVE_PATH)
        }
    }.toTypedArray()

    init {
        registerInvalidatedCallback {
            closeCursor()
        }
        Timber.d("initialized:${hashCode()}")
        var selection: String
        val selectionArgs: MutableList<String>

        when (selectionType) {
            SelectionType.IMAGE -> {
                selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
                selectionArgs =
                    mutableListOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString())
            }
            SelectionType.IMAGE_AND_GIF -> {
                selection =
                    "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? AND ${MediaStore.Files.FileColumns.MIME_TYPE}!=?"
                selectionArgs = mutableListOf(
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
                    "image/gif"
                )
            }
            SelectionType.VIDEO -> {
                selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
                selectionArgs =
                    mutableListOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())
            }
            SelectionType.IMAGE_AND_VIDEO -> {
                selection =
                    "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
                selectionArgs = mutableListOf(
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
                    MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
                )
            }
        }

        if (bucketId != null) {
            selection = "($selection) AND ${MediaStore.Files.FileColumns.BUCKET_ID}=?"
            selectionArgs.add("$bucketId")
        }

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_MODIFIED} desc"

        cursor = context.contentResolver.query(
            contentUri,
            projection,
            selection,
            selectionArgs.toTypedArray(),
            sortOrder
        )
        countChannel.offer(cursor?.count)
        Timber.d("Cursor.count = ${cursor?.count}")

    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Media> {
        try {
            if (cursor == null) {
                return LoadResult.Error(IllegalArgumentException("Cursor is null"))
            }

            val key: Int =
                params.key ?: return LoadResult.Error(IllegalArgumentException("key is null"))


            val prevKey: Int? = if (key == 0) {
                null
            } else {
                max(key - params.loadSize, 0)
            }

            val nextKey: Int? = if (cursor.count == key) {
                null
            } else {
                if (key + params.loadSize >= cursor.count) {
                    null
                } else {
                    key + params.loadSize
                }
            }
            Timber.d("load key=${params.key}, loadSize=${params.loadSize}, prevKey=$prevKey nextKey=$nextKey")
            return LoadResult.Page(
                data = getMediaList(key = key, nextKey = nextKey, loadSize = params.loadSize),
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Media>): Int? {
        val refreshKey: Int? =
            (state.anchorPosition?.div(PickleConstants.DEFAULT_PAGE_SIZE))?.times(PickleConstants.DEFAULT_PAGE_SIZE)
        Timber.d("refreshKey = $refreshKey, anchorPosition = ${state.anchorPosition}")
        return refreshKey
    }

    private fun getMediaList(key: Int, nextKey: Int?, loadSize: Int): List<Media> {
        val mediaList = ArrayList<Media>()
        cursor?.let { cursor ->
            repeat(loadSize) { index ->
                if (cursor.moveToPosition(key + index)) {
                    val relativePath: String? = if (DeviceUtil.isAndroid10Later()) {
                        cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Files.FileColumns.RELATIVE_PATH))
                    } else {
                        null
                    }
                    mediaList.add(
                        Media(
                            contentUri = contentUri,
                            id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)),
                            name = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)),
                            mediaType = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)),
                            mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)),
                            dateModified = cursor.getLongOrNull(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)),
                            dateTaken = cursor.getLongOrNull(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_TAKEN)),
                            dateAdded = cursor.getLongOrNull(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)),
                            width = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.Files.FileColumns.WIDTH)),
                            height = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT)),
                            orientation = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.Files.FileColumns.ORIENTATION)),
                            size = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)),
                            duration = cursor.getLongOrNull(cursor.getColumnIndex(MediaStore.Files.FileColumns.DURATION)),
                            relativePath = relativePath
                        )
                    )
                } else {
                    return@repeat
                }
            }
        }

        Timber.d("key = $key nextKey = $nextKey loadSize = $loadSize mediaList.size = ${mediaList.size}")
        return mediaList

    }

    override val jumpingSupported: Boolean
        get() = true

    enum class SelectionType {
        IMAGE,
        IMAGE_AND_GIF,
        VIDEO,
        IMAGE_AND_VIDEO
    }

    private fun closeCursor() {
        Timber.d("closeCursor")
        if (cursor?.isClosed == false) {
            cursor.close()
        }
    }

}