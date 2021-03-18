package com.charlezz.pickle.sample_java;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.charlezz.pickle.Config;
import com.charlezz.pickle.Pickle;
import com.charlezz.pickle.PickleSingle;
import com.charlezz.pickle.data.entity.Media;
import com.charlezz.pickle.sample_java.databinding.ActivityMainBinding;
import com.xwray.groupie.GroupieAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GroupieAdapter adapter = new GroupieAdapter();
    private ActivityMainBinding binding;

    private Pickle pickle = Pickle.register(this, result -> {
        if (result != null && !result.isEmpty()) {
            adapter.clear();
            ArrayList<SampleItem> items = new ArrayList<>();
            for (Media media : result) {
                items.add(new SampleItem(media));
            }
            adapter.addAll(items);
        }
        binding.noImages.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    });

    private PickleSingle pickleSingle = PickleSingle.register(this, media->{
        if (media != null) {
                    adapter.clear();
            adapter.add(new SampleItem(media));
        }
        binding.noImages.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);
        binding.recyclerView.setAdapter(adapter);

        binding.singleBtn.setOnClickListener(v -> pickleSingle.launch(Config.getDefault()));

        binding.btn.setOnClickListener(v -> pickle.launch(Config.getDefault()));

    }
}