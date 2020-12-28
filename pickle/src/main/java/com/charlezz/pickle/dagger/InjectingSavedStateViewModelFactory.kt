package com.charlezz.pickle.dagger

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import javax.inject.Provider

/**
 * ViewModel들을 인스턴스화 하기 위해 이 클래스를 사용할 수 있다.
 * Fragment/Activity 에서 이 Factory를 주입받아 ViewModel을 생성하는데 사용할 수 있다.
 */
class InjectingSavedStateViewModelFactory constructor(
    private val assistedFactories: Map<Class<out ViewModel>, @JvmSuppressWildcards AssistedSavedStateViewModelFactory<out ViewModel>>,
    private val viewModelProviders: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) {
    /**
     * @AssistedInject 또는 @Inject로 어노테이션이 달린 ViewModel 인스턴스를 작성하고 필요한 종속성을 전달한다.
     */
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

    /**
     * @AssistedInject 생성자와 해당 Factory를 기반으로 ViewModel을 생성한다.
     */
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

    /**
     * 생성자에 @Inject가 있는 일반적인 Dagger 기반의 ViewModel을 생성한다.
     */
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