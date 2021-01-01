package com.charlezz.pickle

import com.charlezz.pickle.util.dagger.PickleScope
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@PickleScope
@Component(
    modules = [
        PickleModule::class,
        AndroidSupportInjectionModule::class,
        PickleViewModelModule::class
    ]
)
interface PickleComponent : AndroidInjector<PickleActivity> {
    @Component.Factory
    abstract class Factory {
        abstract fun create(
            @BindsInstance activity: PickleActivity
        ): AndroidInjector<PickleActivity>
    }
}