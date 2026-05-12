package com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel

import com.adidas.mvi.LoggableState
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
        val irpfError: String? = null,
        val shifts: ImmutableList<ShiftItem> = persistentListOf(),
    ) : EditHospitalState
}
