package com.rodrirepresa.nursera.feature.home.presentation.viewmodel

import com.adidas.mvi.LoggableState

sealed interface HomeState : LoggableState {
    data object Loaded : HomeState

    data object Loading : HomeState
}
