package com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel

import com.adidas.mvi.LoggableState
import com.rodrirepresa.nursera.feature.hospital.presentation.list.ui.HospitalUiModel

sealed interface HospitalState : LoggableState {
    data object Loading : HospitalState

    data class Loaded(val hospitals: List<HospitalUiModel>) : HospitalState
}
