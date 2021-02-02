package com.charlezz.pickle.util

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.charlezz.pickle.PickleActivity
import com.charlezz.pickle.SingleConfig
import com.charlezz.pickle.data.entity.Media

class PickleActivitySingleContract : ActivityResultContract<SingleConfig, Media?>() {
    override fun createIntent(context: Context, input: SingleConfig): Intent {
        return Intent(context, PickleActivity::class.java).apply {
            putExtra(PickleConstants.KEY_CONFIG, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Media? {
        return intent?.getParcelableExtra(PickleConstants.KEY_RESULT_SINGLE)
    }
}