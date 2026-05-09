package com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel

import androidx.compose.runtime.Immutable

@Immutable
data class ExistingShiftUiModel(
    val name: String,
    val schedule: String,
    val hourlyRate: String,
)
