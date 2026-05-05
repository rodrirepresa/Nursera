package com.rodrirepresa.nursera.feature.home.presentation.viewmodel

import com.adidas.mvi.Intent

sealed interface HomeIntent : Intent {
    data object Load : HomeIntent
}
