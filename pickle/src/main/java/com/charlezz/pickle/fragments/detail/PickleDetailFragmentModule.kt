package com.charlezz.pickle.fragments.detail

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.charlezz.pickle.util.dagger.AssistedSavedStateViewModelFactory
import com.charlezz.pickle.util.dagger.FragmentScope
import com.charlezz.pickle.util.dagger.InjectingSavedStateViewModelFactory
import com.charlezz.pickle.util.dagger.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
abstract class PickleDetailFragmentModule {
    companion object{
        @Provides
        @FragmentScope
        fun provideFragmentManager(fragment:PickleDetailFragment):FragmentManager{
            return fragment.childFragmentManager
        }
        @Provides
        @FragmentScope
        fun provideLifecycle(fragment:PickleDetailFragment):Lifecycle{
            return fragment.lifecycle
        }

        @Provides
        fun provideLinearLayoutManager(context: Context):LinearLayoutManager{
            return LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        @Provides
        @FragmentScope
        fun providePagerSnapHelper():PagerSnapHelper{
            return PagerSnapHelper()
        }

        @Provides
        @FragmentScope
        fun provideViewModelProvider(
            fragment: PickleDetailFragment,
            factory: InjectingSavedStateViewModelFactory
        ): ViewModelProvider {
            return ViewModelProvider(fragment, factory.create(fragment))
        }

    }

    @Binds
    @IntoMap
    @ViewModelKey(PickleDetailViewModel::class)
    abstract fun bindsPickleDetailViewModel(factory: PickleDetailViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>
}