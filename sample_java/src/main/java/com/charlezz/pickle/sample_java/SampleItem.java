package com.charlezz.pickle.sample_java;

import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.charlezz.pickle.data.entity.Media;
import com.charlezz.pickle.sample_java.databinding.ItemViewBinding;
import com.xwray.groupie.viewbinding.BindableItem;

/**
 * @author soohwan.ok
 */
public class SampleItem extends BindableItem<ItemViewBinding> {

    private Media media;

    public SampleItem(Media media){
        this.media = media;
    }

    @NonNull
    @Override
    protected ItemViewBinding initializeViewBinding(@NonNull View view) {
        return ItemViewBinding.bind(view);
    }

    @Override
    public void bind(@NonNull ItemViewBinding viewBinding, int position) {
        Glide.with(viewBinding.imageView).load(media.getUri()).into(viewBinding.imageView);
        viewBinding.executePendingBindings();
    }

    @Override
    public int getLayout() {
        return R.layout.item_view;
    }
}
