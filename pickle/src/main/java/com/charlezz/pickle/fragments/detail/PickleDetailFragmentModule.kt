package com.charlezz.pickle.fragments.detail

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.charlezz.pickle.R
import com.charlezz.pickle.uimodel.ToolbarViewModel
import com.charlezz.pickle.util.DeviceUtil
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

        @Provides
        @FragmentScope
        fun provideToolbarViewModel(context:Context): ToolbarViewModel {
            return ToolbarViewModel(context).apply {
                if(DeviceUtil.isAndroid5Later()){
                    backgroundColorRes = R.color.black_a50
                }else{
                    backgroundColorRes = R.color.black
                }

                titleTextColorRes = R.color.white
                alignCenter = true
                navigationIcon = ContextCompat.getDrawable(context, R.drawable.ico_pickle_close)
            }
        }
    }

    @Binds
    @IntoMap
    @ViewModelKey(PickleDetailViewModel::class)
    abstract fun bindsPickleDetailViewModel(factory: PickleDetailViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>
}