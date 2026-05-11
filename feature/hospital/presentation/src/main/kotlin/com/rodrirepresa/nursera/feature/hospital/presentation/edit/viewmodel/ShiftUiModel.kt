package com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel

import androidx.compose.runtime.Immutable
import java.util.UUID

@Immutable
data class ShiftUiModel(
    val id: UUID,
    val name: String,
    val schedule: String,
    val hourlyRate: String,
)
