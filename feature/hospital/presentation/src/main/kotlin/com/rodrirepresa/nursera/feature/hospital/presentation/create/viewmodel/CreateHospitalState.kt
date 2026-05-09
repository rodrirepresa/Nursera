package com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel

import com.adidas.mvi.LoggableState
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface CreateHospitalState : LoggableState {
    data class Loaded(
        val name: String = "",
        val nameError: String? = null,
        val irpf: String = "",
        val irpfError: String? = null,
        val shifts: ImmutableList<ShiftFormUiState> = persistentListOf(ShiftFormUiState()),
        val canSave: Boolean = false,
    ) : CreateHospitalState

    data object Saving : CreateHospitalState
}
