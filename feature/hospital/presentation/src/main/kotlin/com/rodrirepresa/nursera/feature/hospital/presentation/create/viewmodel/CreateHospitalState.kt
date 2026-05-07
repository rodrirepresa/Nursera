package com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel

import com.adidas.mvi.LoggableState

sealed interface CreateHospitalState : LoggableState {
    data object Idle : CreateHospitalState

    data object Saving : CreateHospitalState
}
