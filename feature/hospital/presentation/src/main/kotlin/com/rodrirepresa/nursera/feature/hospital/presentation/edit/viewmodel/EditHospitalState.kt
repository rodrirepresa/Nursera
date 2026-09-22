package com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel

import androidx.annotation.StringRes
import com.adidas.mvi.LoggableState
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.util.UUID

sealed interface EditHospitalState : LoggableState {
    data object Loading : EditHospitalState

    data object Error : EditHospitalState

    data class Loaded(
        val hospitalId: UUID,
        val hospitalName: String = "",
        val hospitalColor: Int = 0,
        val originalIrpf: String = "",
        val irpf: String = "",
        @StringRes val irpfError: Int? = null,
        val shifts: ImmutableList<ShiftItem> = persistentListOf(),
        val shiftForm: ShiftFormUiState? = null,
        val isShiftFormSaving: Boolean = false,
        val canSaveShiftForm: Boolean = false,
    ) : EditHospitalState
}
