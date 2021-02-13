package com.charlezz.pickle.fragments.folder

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.charlezz.pickle.Config
import com.charlezz.pickle.R
import com.charlezz.pickle.uimodel.ToolbarViewModel
import com.charlezz.pickle.util.MeasureUtil
import com.charlezz.pickle.util.dagger.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Provider

@Module
abstract class PickleAlbumFragmentModule {
    companion object {

        @Provides
        fun provideGridLayoutManager(
            context: Context,
            @ColumnCount columnCount: Provider<Int>
        ): GridLayoutManager {
            return GridLayoutManager(context, columnCount.get())
        }

        @Provides
        @ColumnCount
        fun provideColumnCount(context: Context): Int {
            return MeasureUtil.getProperSpanCount(context, R.dimen.pickle_column_width)
        }

        @Provides
        fun provideViewModelProvider(
            fragment:PickleAlbumFragment,
            factory: InjectingSavedStateViewModelFactory
        ):ViewModelProvider{
            return ViewModelProvider(fragment, factory.create(fragment))
        }
        @Provides
        @FragmentScope
        fun provideToolbarViewModel(context:Context,config:Config): ToolbarViewModel {
            return ToolbarViewModel(context).apply {
                title = context.getString(config.albumsTextRes)
            }
        }
    }

    @Binds
    @IntoMap
    @ViewModelKey(PickleAlbumViewModel::class)
    abstract fun bindsPickleFolderViewModel(factory: PickleAlbumViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>
}