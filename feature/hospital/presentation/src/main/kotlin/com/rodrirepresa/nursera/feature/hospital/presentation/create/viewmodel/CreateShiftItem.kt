package com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel

import androidx.compose.runtime.Immutable
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState

@Immutable
data class CreateShiftItem(
    val form: ShiftFormUiState,
    val isSelected: Boolean = false,
    val id: String,
) {
    val listKey: String get() = id
}
