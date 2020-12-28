package com.charlezz.picklesample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.charlezz.pickle.Config
import com.charlezz.pickle.getPickle
import com.charlezz.picklesample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    private val adapter = MainAdapter()

    private val launcher = getPickle { mediaList ->
        adapter.setImages(mediaList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.recyclerView.adapter = adapter
        binding.btn.setOnClickListener {
            launcher.launch(Config.default)
        }

    }

}
