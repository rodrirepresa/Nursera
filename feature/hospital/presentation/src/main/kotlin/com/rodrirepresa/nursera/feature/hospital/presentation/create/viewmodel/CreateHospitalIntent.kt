package com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel

import com.adidas.mvi.Intent
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState

sealed interface CreateHospitalIntent : Intent {
    data object Load : CreateHospitalIntent

    data object NavigateBack : CreateHospitalIntent

    data class UpdateName(val value: String) : CreateHospitalIntent

    data class UpdateIrpf(val value: String) : CreateHospitalIntent

    data object OpenShiftSheet : CreateHospitalIntent

    data object DismissShiftSheet : CreateHospitalIntent

    data class UpdateNewShift(val shift: ShiftFormUiState) : CreateHospitalIntent

    data object SaveShift : CreateHospitalIntent

    data class ToggleShiftSelection(val id: String) : CreateHospitalIntent

    data object DeleteSelectedShifts : CreateHospitalIntent

    data object Save : CreateHospitalIntent
}
