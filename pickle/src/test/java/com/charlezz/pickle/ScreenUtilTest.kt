package com.charlezz.pickle

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.charlezz.pickle.util.ScreenUtil
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.random.Random

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "xxxhdpi")
class ScreenUtilTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun dpToPx() {
        Assert.assertEquals(ScreenUtil.dpToPx(context, 10f), 40)
    }

    @Test
    fun columnCount() {
        val w = context.resources.displayMetrics.widthPixels
        val randomColumnCount = Random.nextInt(5) + 1
        Assert.assertEquals(
            ScreenUtil.getSpanCount(context, (w / randomColumnCount)),
            randomColumnCount
        )
        /**
         * 가로 1280
         * 120dp = 120 * 4 px = 480px
         * 1280 / 480 = 2.66
         */
        Assert.assertEquals(
            ScreenUtil.getProperSpanCount(
                context,
                R.dimen.pickle_column_width
            ), (2.66f).toInt()) // 2)
    }

}