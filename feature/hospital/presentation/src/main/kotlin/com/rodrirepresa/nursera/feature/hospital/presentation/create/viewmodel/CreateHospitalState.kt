package com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel

import androidx.annotation.StringRes
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
        @StringRes val nameError: Int? = null,
        val irpf: String = "",
        @StringRes val irpfError: Int? = null,
        val shifts: ImmutableList<CreateShiftItem> = persistentListOf(),
        val shiftForm: ShiftFormUiState? = null,
        val isShiftFormSaving: Boolean = false,
        val canSaveShiftForm: Boolean = false,
        val canSave: Boolean = false,
        val isSaving: Boolean = false,
    ) : CreateHospitalState
}
