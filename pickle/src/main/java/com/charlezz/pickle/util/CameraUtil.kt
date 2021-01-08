package com.charlezz.pickle.util

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import androidx.core.content.FileProvider
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CameraUtil constructor(
    val context: Context,
    val envDir: String,
    val dirToSave: String,
) {

    var currentImagePath: String? = null

    /**
     * - Android 10 or later : App External file directory
     * - Android 9 or Earlier : Public directory
     */
    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = if (DeviceUtil.isAndroid10Later()) {
            context.getExternalFilesDir(envDir)
                ?: throw IllegalArgumentException("Can't get External file directory($envDir)")

        } else {
            Environment.getExternalStoragePublicDirectory(envDir + File.separator + dirToSave)
        }

        return File.createTempFile(
            "${timeStamp}_",
            ".jpg",
            /* directory */ storageDir
        ).apply {
            currentImagePath = absolutePath
            Timber.d("path = $absolutePath")
        }
    }

    fun hasCamera(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(context.packageManager) != null
        // reference: https://stackoverflow.com/a/37620935
    }

    fun prepareImageCapture(readyToLaunch: (Uri) -> Unit, fallback:(IOException)->Unit) {
        val imageFile: File? = try {
            createImageFile()
        } catch (e: IOException) {
            fallback.invoke(e)
            Timber.w(e)
            null
        }
        imageFile?.let { file ->
            val imageUri: Uri = FileProvider.getUriForFile(
                context,
                PickleConstants.getAuthority(context),
                file
            )
            readyToLaunch.invoke(imageUri)
        }
    }

    fun saveImageToMediaStore(callback: () -> Unit) {
        currentImagePath?.let { path ->
            val file = File(path)
            if (DeviceUtil.isAndroid10Later()) {
                val contentUri =
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                val contentResolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
                    put(MediaStore.Images.Media.RELATIVE_PATH, "$envDir${File.separator}$dirToSave")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
                val imageUri: Uri? = contentResolver.insert(contentUri, contentValues)
                Timber.d("insertedUri = $imageUri")
                if (imageUri != null) {
                    //copy file
                    context.contentResolver.openFileDescriptor(imageUri, "w")
                        .use { parcelFileDescriptor ->
                            ParcelFileDescriptor.AutoCloseOutputStream(parcelFileDescriptor)
                                .write(file.readBytes())
                        }

                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(imageUri, contentValues, null, null)
                }

            }
            MediaScannerConnection.scanFile(
                context,
                arrayOf(currentImagePath),
                null
            ) { path, uri ->
                Timber.d("MediaScannerConnection path = $path, uri = $uri")
                callback.invoke()
            }
        } ?: Timber.w("No Image to Save")
    }

}