package com.charlezz.picklesample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.charlezz.pickle.Config
import com.charlezz.pickle.SingleConfig
import com.charlezz.pickle.data.entity.Media
import com.charlezz.pickle.getPickle
import com.charlezz.pickle.getPickleForSingle
import com.charlezz.picklesample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val adapter = MainAdapter()

    private val singleLauncher = getPickleForSingle { media: Media? ->
        media?.let { adapter.setImages(listOf(media)) }
    }

    private val launcher = getPickle { mediaList:List<Media>? ->
        mediaList?.let {
            adapter.setImages(it)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.singleBtn.setOnClickListener {
            singleLauncher.launch(SingleConfig.default)
        }

        binding.btn.setOnClickListener {
            launcher.launch(Config.default.apply {
                this.debugMode = false
            })
        }

        if (savedInstanceState == null) {
            singleLauncher.launch(SingleConfig.default)
        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                binding.noImages.visibility =
                    if (adapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })



    }


}
