package com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel

import com.adidas.mvi.Intent
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState

sealed interface EditHospitalIntent : Intent {
    data object Load : EditHospitalIntent

    data object NavigateBack : EditHospitalIntent

    data class UpdateIrpf(val value: String) : EditHospitalIntent

    data object AddShift : EditHospitalIntent

    data class UpdateNewShift(val shift: ShiftFormUiState) : EditHospitalIntent

    // data class RemoveShift(val index: Int) : EditHospitalIntent

    data class ToggleExistingShiftSelection(val index: Int) : EditHospitalIntent

    data object DeleteSelectedShifts : EditHospitalIntent

    data object SaveShift : EditHospitalIntent
}
