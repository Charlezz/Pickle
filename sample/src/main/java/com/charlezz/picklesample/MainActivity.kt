package com.charlezz.picklesample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.charlezz.pickle.Config
import com.charlezz.pickle.Pickle
import com.charlezz.pickle.PickleSingle
import com.charlezz.pickle.data.entity.Media
import com.charlezz.picklesample.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val adapter = MainAdapter()

    private val singleLauncher = PickleSingle.register(this, object:PickleSingle.Callback{
        override fun onResult(media: Media?) {
            media?.let { adapter.setImages(listOf(media)) }
        }
    })

    private val launcher = Pickle.register(this, object:Pickle.Callback{
        override fun onResult(result: ArrayList<Media>) {
            adapter.setImages(result)
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.singleBtn.setOnClickListener {
            singleLauncher.launch(Config.getDefault())
        }

        binding.btn.setOnClickListener {
            launcher.launch(Config.getDefault().apply {
                this.debugMode = false
            })
        }

        if (savedInstanceState == null) {
//            singleLauncher.launch(Config.default)
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
