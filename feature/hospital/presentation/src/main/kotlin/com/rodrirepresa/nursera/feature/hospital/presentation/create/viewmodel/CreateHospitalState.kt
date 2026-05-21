package com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel

import com.adidas.mvi.LoggableState
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface CreateHospitalState : LoggableState {
    data object Loading : CreateHospitalState

    data object Error : CreateHospitalState

    data class Loaded(
        val hospitalColor: Int = 0,
        val name: String = "",
        val nameError: String? = null,
        val irpf: String = "",
        val irpfError: String? = null,
        val shifts: ImmutableList<CreateShiftItem> = persistentListOf(),
        val shiftForm: ShiftFormUiState? = null,
        val isShiftFormSaving: Boolean = false,
        val canSaveShiftForm: Boolean = false,
        val canSave: Boolean = false,
        val isSaving: Boolean = false,
    ) : CreateHospitalState
}
