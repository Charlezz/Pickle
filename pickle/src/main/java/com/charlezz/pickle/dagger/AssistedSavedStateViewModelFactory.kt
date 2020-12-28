package com.charlezz.pickle.dagger

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

/**
 * SavedStateHandle을 포함하는 ViewModel을 생성하기 위한 범용적인 Factory
 * 이 Factory는 하나의 InjectingSavedStateViewModelFactory에 모든 ViewModel을 가질 수 있도록 한다.
 */
interface AssistedSavedStateViewModelFactory<T : ViewModel> {
    fun create(savedStateHandle: SavedStateHandle): T
}