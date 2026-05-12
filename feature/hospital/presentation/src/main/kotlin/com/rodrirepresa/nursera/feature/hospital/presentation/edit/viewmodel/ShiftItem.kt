package com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel

import androidx.compose.runtime.Immutable
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState

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

    @Immutable
    data class Form(
        val form: ShiftFormUiState = ShiftFormUiState(),
        val canSave: Boolean = false,
        val isSaving: Boolean = false,
        val isVisible: Boolean = false,
    ) : ShiftItem {
        override val listKey: String get() = "form"
    }
}
