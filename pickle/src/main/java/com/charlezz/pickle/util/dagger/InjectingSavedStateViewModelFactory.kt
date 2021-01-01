package com.charlezz.pickle.util.dagger

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import javax.inject.Provider

class InjectingSavedStateViewModelFactory constructor(
    private val assistedFactories: Map<Class<out ViewModel>, @JvmSuppressWildcards AssistedSavedStateViewModelFactory<out ViewModel>>,
    private val viewModelProviders: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) {
    fun create(owner: SavedStateRegistryOwner, defaultArgs: Bundle? = null) =
        object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
            override fun <T : ViewModel?> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                val viewModel =
                    createAssistedInjectViewModel(modelClass, handle)
                        ?: createInjectViewModel(modelClass)
                        ?: throw IllegalArgumentException("Unknown model class $modelClass")

                try {
                    @Suppress("UNCHECKED_CAST")
                    return viewModel as T
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
        }

    private fun <T : ViewModel?> createAssistedInjectViewModel(
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): ViewModel? {
        val creator = assistedFactories[modelClass]
            ?: assistedFactories.asIterable()
                .firstOrNull { modelClass.isAssignableFrom(it.key) }?.value
            ?: return null

        return creator.create(handle)
    }

    private fun <T : ViewModel?> createInjectViewModel(
        modelClass: Class<T>
    ): ViewModel? {
        val creator = viewModelProviders[modelClass]
            ?: viewModelProviders.asIterable()
                .firstOrNull { modelClass.isAssignableFrom(it.key) }?.value
            ?: return null

        return creator.get()
    }
}