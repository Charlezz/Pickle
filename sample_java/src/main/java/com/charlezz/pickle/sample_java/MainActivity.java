package com.charlezz.pickle.sample_java;

import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.charlezz.pickle.Config;
import com.charlezz.pickle.SingleConfig;
import com.charlezz.pickle.data.entity.Media;
import com.charlezz.pickle.sample_java.databinding.ActivityMainBinding;
import com.charlezz.pickle.util.PickleActivityContract;
import com.charlezz.pickle.util.PickleActivitySingleContract;
import com.xwray.groupie.GroupieAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GroupieAdapter adapter = new GroupieAdapter();
    private ActivityMainBinding binding;

    private ActivityResultLauncher<SingleConfig> singleLauncher =
            registerForActivityResult(new PickleActivitySingleContract(), media -> {
                if (media != null) {
                    adapter.clear();
                    adapter.add(new SampleItem(media));
                }
                binding.noImages.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            });

    private ActivityResultLauncher<Config> launcher =
            registerForActivityResult(new PickleActivityContract(), result -> {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);
        binding.recyclerView.setAdapter(adapter);

        binding.singleBtn.setOnClickListener(v -> singleLauncher.launch(SingleConfig.getDefault()));

        binding.btn.setOnClickListener(v -> launcher.launch(Config.getDefault()));

    }
}