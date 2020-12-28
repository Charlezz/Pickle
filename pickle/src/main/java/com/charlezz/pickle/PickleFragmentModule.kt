package com.charlezz.pickle

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import com.charlezz.pickle.dagger.FragmentScope
import com.charlezz.pickle.data.entity.CameraItem
import com.charlezz.pickle.util.recyclerview.GridSpaceDecoration
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class PickleFragmentModule {
    companion object {
        @Provides
        @FragmentScope
        fun provideLayoutManager(context: Context): GridLayoutManager =
            GridLayoutManager(context, PickleConstants.DEFAULT_SPAN_COUNT)

        @Provides
        @FragmentScope
        fun provideGridSpaceDecoration() =
            GridSpaceDecoration(spanCount = PickleConstants.DEFAULT_SPAN_COUNT)
    }

    @Binds
    abstract fun bindsCameraItemOnItemClickListener(fragment: PickleFragment): CameraItem.OnItemClickListener
}