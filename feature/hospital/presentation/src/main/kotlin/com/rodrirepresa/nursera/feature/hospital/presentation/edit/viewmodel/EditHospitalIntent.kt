package com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel

import com.adidas.mvi.Intent
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import java.util.UUID

sealed interface EditHospitalIntent : Intent {
    data class Load(val hospitalId: UUID) : EditHospitalIntent

    data class UpdateIrpf(val value: String) : EditHospitalIntent

    data object AddShift : EditHospitalIntent

    data class UpdateShiftAt(val index: Int, val shift: ShiftFormUiState) : EditHospitalIntent

    data class RemoveShift(val index: Int) : EditHospitalIntent

    data object Save : EditHospitalIntent
}
