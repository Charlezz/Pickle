package com.charlezz.pickle

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.charlezz.pickle.data.entity.Media

class PickleActivityContract : ActivityResultContract<Config, ArrayList<Media>>() {
    override fun createIntent(context: Context, input: Config?): Intent {
        return Intent(context, PickleActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ArrayList<Media> {
        return intent?.getParcelableArrayListExtra("media") ?: ArrayList()
    }
}