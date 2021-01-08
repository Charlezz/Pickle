package com.charlezz.pickle.fragments.folder

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.os.Environment
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import com.charlezz.pickle.R
import com.charlezz.pickle.data.entity.Album
import com.charlezz.pickle.util.DeviceUtil
import com.charlezz.pickle.util.PickleConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

@SuppressLint("InlinedApi")
class PickleAlbumRepository(private val context: Context) {

    private val contentUri = PickleConstants.getContentUri()

    private val folderMap = LinkedHashMap<Long?, Album>()

    private val projection = arrayListOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.BUCKET_ID,
        MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
        MediaStore.Files.FileColumns.DATE_MODIFIED
    ).apply {
        if (DeviceUtil.isAndroid10Later()) {
            add(MediaStore.Files.FileColumns.RELATIVE_PATH)
        } else {
            add(MediaStore.Files.FileColumns.DATA)
        }
    }.toTypedArray()
    private val selection =
        "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"

    private val selectionArgs: Array<String> = arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
    )

    private val sortOrder = "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"

    suspend fun load(): Flow<HashMap<Long?, Album>> {
        return flow {
            //Recent
            val recentMediaCursor:Cursor? = context.contentResolver.query(
                contentUri,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )

            if (recentMediaCursor?.moveToNext() == true) {
                folderMap[null] = Album(
                    contentUri = contentUri,
                    recentMediaId = recentMediaCursor.getLong(
                        recentMediaCursor.getColumnIndex(
                            MediaStore.Files.FileColumns._ID
                        )
                    ),
                    bucketId = null,
                    name = context.getString(R.string.recent),
                    count = recentMediaCursor.count,
                    order = PickleConstants.FOLDER_ORDER_RECENT,
                    relativePath = recentMediaCursor.getStringOrNull(
                        recentMediaCursor.getColumnIndex(
                            MediaStore.Files.FileColumns.RELATIVE_PATH
                        )
                    ),
                    data = recentMediaCursor.getStringOrNull(
                        recentMediaCursor.getColumnIndex(
                            MediaStore.Files.FileColumns.DATA
                        )
                    )
                )
            }
            recentMediaCursor?.close()

            //Retrieve folder list

            val cursor: Cursor? = context.contentResolver.query(
                contentUri,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )
            cursor?.let { c ->
                while (c.moveToNext()) {
                    val bucketId =
                        c.getLong(c.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_ID))
                    if (folderMap.contains(bucketId)) {
                        //hit
                        Timber.d("Already hit::bucketId = $bucketId")
                        folderMap[bucketId]?.increaseCount()
                        continue
                    } else {
                        //add
                        val id = c.getLong(c.getColumnIndex(MediaStore.Files.FileColumns._ID))
                        val name =
                            c.getString(c.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME))
                        val relativePath =
                            c.getStringOrNull(c.getColumnIndex(MediaStore.Files.FileColumns.RELATIVE_PATH))
                        val data =
                            c.getStringOrNull(c.getColumnIndex(MediaStore.Files.FileColumns.DATA))

                        var order =
                            c.getLong(c.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED))

                        if (DeviceUtil.isAndroid10Later()) {
                            if (relativePath == "DCIM/Camera/") {
                                order = PickleConstants.FOLDER_ORDER_CAMERA
                            } else if (relativePath == "Download/") {
                                order = PickleConstants.FOLDER_ORDER_DOWNLOAD
                            }
                        }

                        if (!DeviceUtil.isAndroid10Later()) {
                            if (data == Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_DCIM
                                ).toString()
                            ) {
                                order = PickleConstants.FOLDER_ORDER_CAMERA
                            } else if (data == Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_DOWNLOADS
                                ).toString()
                            ) {
                                order = PickleConstants.FOLDER_ORDER_DOWNLOAD
                            }

                        }

                        folderMap[bucketId] = Album(
                            contentUri = contentUri,
                            recentMediaId = id,
                            bucketId = bucketId,
                            name = name,
                            count = 1,
                            order = order,
                            relativePath = relativePath,
                            data = data,
                        )
                        Timber.d("added::bucketId = $bucketId, name = $name, relativePath = $relativePath, data = $data")
                    }
                    emit(folderMap)
                }
            }
            cursor?.close()
        }
    }
}