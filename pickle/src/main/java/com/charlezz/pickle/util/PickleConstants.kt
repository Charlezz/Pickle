package com.charlezz.pickle.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

object PickleConstants {

    const val THUMBNAIL_TRANSITION_NAME = "thumbnail"
    const val DEFAULT_SPAN_COUNT = 3
    const val DEFAULT_PAGE_SIZE = 30
    const val DEFAULT_POSITION = 0
    const val NO_POSITION = -1
    const val KEY_CONFIG = "config"

    const val KEY_RESULT_SINGLE = "result_media"
    const val KEY_RESULT_MULTIPLE = "result_media_list"


    const val FOLDER_ORDER_RECENT = Long.MAX_VALUE
    const val FOLDER_ORDER_CAMERA = Long.MAX_VALUE-1
    const val FOLDER_ORDER_DOWNLOAD = Long.MAX_VALUE-2
    fun getAuthority(context: Context) = "${context.packageName}.pickle.fileprovider"

    fun getContentUri(): Uri {
        return if (DeviceUtil.isAndroid10Later()) {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        }else{
            MediaStore.Files.getContentUri("external")
        }
    }
}