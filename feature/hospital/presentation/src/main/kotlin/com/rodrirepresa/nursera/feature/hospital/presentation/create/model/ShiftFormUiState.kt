package com.rodrirepresa.nursera.feature.hospital.presentation.create.model

import androidx.compose.runtime.Immutable

@Immutable
data class ShiftFormUiState(
    val name: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val hourlyRate: String = "",
    val nameError: String? = null,
    val startTimeError: String? = null,
    val endTimeError: String? = null,
    val hourlyRateError: String? = null,
)
