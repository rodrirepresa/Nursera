package com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import java.util.UUID

@Immutable
data class HospitalPickerItem(
    val id: UUID,
    val name: String,
    val color: Int,
)

@Immutable
data class ShiftPickerItem(
    val id: UUID,
    val name: String,
    val startTime: String,
    val endTime: String,
)

sealed interface AddShiftSheetUiState {
    @Immutable
    data class HospitalList(val hospitals: ImmutableList<HospitalPickerItem>) : AddShiftSheetUiState

    @Immutable
    data class ShiftList(
        val hospital: HospitalPickerItem,
        val shifts: ImmutableList<ShiftPickerItem>,
    ) : AddShiftSheetUiState
}
