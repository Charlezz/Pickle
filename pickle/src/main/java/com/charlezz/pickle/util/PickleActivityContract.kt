package com.charlezz.pickle.util

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.charlezz.pickle.Config
import com.charlezz.pickle.PickleActivity
import com.charlezz.pickle.data.entity.Media

class PickleActivityContract : ActivityResultContract<Config, ArrayList<Media>>() {
    override fun createIntent(context: Context, input: Config?): Intent {
        return Intent(context, PickleActivity::class.java).apply {
            putExtra(PickleConstants.KEY_CONFIG, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ArrayList<Media> {
        return intent?.getParcelableArrayListExtra(PickleConstants.KEY_RESULT_MULTIPLE) ?: ArrayList()
    }
}