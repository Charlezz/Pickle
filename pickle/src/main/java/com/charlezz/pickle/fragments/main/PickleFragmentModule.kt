package com.charlezz.pickle.fragments.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.charlezz.pickle.data.entity.CameraItem
import com.charlezz.pickle.uimodel.ToolbarViewModel
import com.charlezz.pickle.util.MeasureUtil
import com.charlezz.pickle.util.PickleConstants
import com.charlezz.pickle.util.dagger.AssistedSavedStateViewModelFactory
import com.charlezz.pickle.util.dagger.FragmentScope
import com.charlezz.pickle.util.dagger.InjectingSavedStateViewModelFactory
import com.charlezz.pickle.util.dagger.ViewModelKey
import com.charlezz.pickle.util.recyclerview.GridSpaceDecoration
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
abstract class PickleFragmentModule {
    companion object {
        @Provides
        fun provideLayoutManager(context: Context): GridLayoutManager =
            GridLayoutManager(context, PickleConstants.DEFAULT_SPAN_COUNT)

        @Provides
        @FragmentScope
        fun provideGridSpaceDecoration(context: Context) =
            GridSpaceDecoration(
                spanCount = PickleConstants.DEFAULT_SPAN_COUNT,
                space = MeasureUtil.dpToPx(context, 2f)
            )

        @Provides
        @FragmentScope
        fun provideViewModelProvider(
            fragment: PickleFragment,
            factory: InjectingSavedStateViewModelFactory
        ) = ViewModelProvider(fragment, factory.create(fragment))

        @Provides
        @FragmentScope
        fun provideToolbarViewModel(context:Context):ToolbarViewModel{
            return ToolbarViewModel(context)
        }


    }

    @Binds
    @IntoMap
    @ViewModelKey(PickleViewModel::class)
    abstract fun bindsPickleViewModel(factory: PickleViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

    @Binds
    abstract fun bindsCameraItemOnItemClickListener(fragment: PickleFragment): CameraItem.OnItemClickListener
}