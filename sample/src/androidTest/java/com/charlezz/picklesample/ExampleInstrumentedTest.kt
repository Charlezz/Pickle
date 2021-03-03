package com.charlezz.picklesample

import android.provider.MediaStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureTimeMillis

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.charlezz.picklesample", appContext.packageName)
    }

    @Test
    fun cursorTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val fileUri = MediaStore.Files.getContentUri("external")
        val selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?"
        val selectionArgs = arrayOf("${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE}")

        var queryTime = measureTimeMillis {
            context.contentResolver.query(fileUri, null, selection, selectionArgs, null)
        }

        println("queryTime #1 : $queryTime")

        queryTime = measureTimeMillis {
            context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,null,null)
        }

        println("queryTime #2 : $queryTime")



    }
}
