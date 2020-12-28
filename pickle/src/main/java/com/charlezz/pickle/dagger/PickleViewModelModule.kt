package com.charlezz.pickle.dagger

import androidx.lifecycle.ViewModel
import com.charlezz.pickle.PickleSharedViewModel
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

    //일반 뷰모델들의 멀티 바인딩
    @Multibinds
    abstract fun bindsViewModels(): Map<Class<out ViewModel>, @JvmSuppressWildcards ViewModel>

    //AssistedInject로 관리하는 ViewModel Factory 멀티바인딩
    @Multibinds
    abstract fun bindsAssistedViewModels(): Map<Class<out ViewModel>, @JvmSuppressWildcards AssistedSavedStateViewModelFactory<out ViewModel>>

    @Binds
    @IntoMap
    @ViewModelKey(PickleSharedViewModel::class)
    abstract fun bindsPickleSharedViewModel(factory: PickleSharedViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>
}