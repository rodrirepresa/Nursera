package com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel

import com.adidas.mvi.LoggableState
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

sealed interface EditHospitalState : LoggableState {
    data object Loading : EditHospitalState

    data object Error : EditHospitalState

    data class Loaded(
        val hospitalId: java.util.UUID,
        val hospitalName: String = "",
        val hospitalColor: Int = 0,
        val originalIrpf: String = "",
        val irpf: String = "",
        val irpfError: String? = null,
        val existingShifts: ImmutableList<ShiftUiModel> = persistentListOf(),
        val selectedShiftIndices: ImmutableSet<Int> = persistentSetOf(),
        val newShift: ShiftFormUiState? = null,
        val isSaving: Boolean = false,
        val canSave: Boolean = false,
    ) : EditHospitalState
}
