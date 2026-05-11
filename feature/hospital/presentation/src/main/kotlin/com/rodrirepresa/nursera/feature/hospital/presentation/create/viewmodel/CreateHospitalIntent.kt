package com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel

import com.adidas.mvi.Intent
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState

sealed interface CreateHospitalIntent : Intent {
    data object NavigateBack : CreateHospitalIntent

    data class UpdateName(val value: String) : CreateHospitalIntent

    data class UpdateIrpf(val value: String) : CreateHospitalIntent

    data object AddShift : CreateHospitalIntent

    data class UpdateShiftAt(val index: Int, val shift: ShiftFormUiState) : CreateHospitalIntent

    data class RemoveShift(val index: Int) : CreateHospitalIntent

    data object Save : CreateHospitalIntent
}
