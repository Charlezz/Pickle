package com.charlezz.pickle.dagger

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.charlezz.pickle.PickleActivity
import com.charlezz.pickle.PickleFragment
import com.charlezz.pickle.PickleFragmentModule
import com.charlezz.pickle.data.repository.AppPickleRepository
import com.charlezz.pickle.data.repository.PickleRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
abstract class PickleModule {

    @Binds
    @PickleScope
    abstract fun bindsContext(activity: PickleActivity): Context

    companion object {

        @Provides
        fun provideApplication(activity: PickleActivity): Application {
            return activity.application
        }

        @Provides
        fun provideAppPickleRepository(context: Context): PickleRepository =
            AppPickleRepository(context)


        @Provides
        @PickleScope
        @SharedViewModelProvider
        fun provideViewModelProvider(
            activity: PickleActivity,
            factory: InjectingSavedStateViewModelFactory
        ): ViewModelProvider {
            return ViewModelProvider(activity, factory.create(activity))
        }

    }

    @FragmentScope
    @ContributesAndroidInjector(modules = [PickleFragmentModule::class])
    abstract fun bindsPickleFragment(): PickleFragment
}
