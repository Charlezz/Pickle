package com.charlezz.pickle

import androidx.lifecycle.ViewModel
import com.charlezz.pickle.util.dagger.AssistedSavedStateViewModelFactory
import com.charlezz.pickle.util.dagger.InjectingSavedStateViewModelFactory
import com.charlezz.pickle.util.dagger.ViewModelKey
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.Multibinds
import javax.inject.Provider

@AssistedModule
@Module(includes = [AssistedInject_PickleViewModelModule::class])
abstract class PickleViewModelModule {

    companion object{
        @Provides
        fun provideInjectingSavedStateViewModelFactory(assistedFactories: Map<Class<out ViewModel>, @JvmSuppressWildcards AssistedSavedStateViewModelFactory<out ViewModel>>,
                                                       viewModelProviders: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>): InjectingSavedStateViewModelFactory {
            return InjectingSavedStateViewModelFactory(assistedFactories,viewModelProviders)
        }
    }

    @Multibinds
    abstract fun bindsViewModels(): Map<Class<out ViewModel>, @JvmSuppressWildcards ViewModel>

    @Multibinds
    abstract fun bindsAssistedViewModels(): Map<Class<out ViewModel>, @JvmSuppressWildcards AssistedSavedStateViewModelFactory<out ViewModel>>

    @Binds
    @IntoMap
    @ViewModelKey(PickleSharedViewModel::class)
    abstract fun bindsPickleSharedViewModel(factory: PickleSharedViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

}