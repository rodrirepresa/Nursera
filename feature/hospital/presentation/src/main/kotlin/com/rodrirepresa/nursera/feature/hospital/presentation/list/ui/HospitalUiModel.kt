package com.rodrirepresa.nursera.feature.hospital.presentation.list.ui

import androidx.compose.runtime.Immutable
import java.util.UUID

@Immutable
data class HospitalUiModel(
    val id: UUID,
    val name: String,
    val color: Int,
    val irpf: String,
    val shifts: List<ShiftTypeUiModel>,
)

@Immutable
data class ShiftTypeUiModel(
    val name: String,
    val schedule: String,
    val hourlyRate: String,
)
