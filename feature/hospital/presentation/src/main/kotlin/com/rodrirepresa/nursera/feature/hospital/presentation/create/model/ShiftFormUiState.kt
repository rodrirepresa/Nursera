package com.rodrirepresa.nursera.feature.hospital.presentation.create.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class ShiftFormUiState(
    val name: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val hourlyRate: String = "",
    @StringRes val nameError: Int? = null,
    @StringRes val startTimeError: Int? = null,
    @StringRes val endTimeError: Int? = null,
    @StringRes val hourlyRateError: Int? = null,
)
