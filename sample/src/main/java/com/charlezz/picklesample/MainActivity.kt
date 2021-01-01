package com.charlezz.picklesample

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.charlezz.pickle.Config
import com.charlezz.pickle.data.entity.Media
import com.charlezz.pickle.getPickle
import com.charlezz.picklesample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val adapter = MainAdapter()

    private val launcher = getPickle { mediaList:List<Media> ->
        Log.d("MainActivity","list.size = ${mediaList.size} $mediaList")
        adapter.setImages(mediaList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.recyclerView.adapter = adapter
        binding.btn.setOnClickListener {
            launcher.launch(Config.default.apply {
                title = "PickleSample"
            })
        }

        if(savedInstanceState==null){
//            launcher.launch(Config.default.apply {
//                title = "Pickle ${BuildConfig.VERSION_NAME}"
//            })
        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                binding.noImages.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

    }


}
