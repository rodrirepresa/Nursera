package com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel

import androidx.compose.runtime.Immutable

@Immutable
sealed interface ShiftItem {
    val listKey: String

    @Immutable
    data class Existing(
        val model: ShiftUiModel,
        val isSelected: Boolean = false,
    ) : ShiftItem {
        override val listKey: String get() = model.id.toString()
    }
}
