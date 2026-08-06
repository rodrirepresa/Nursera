package com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel

import androidx.compose.runtime.Immutable
import java.time.LocalTime

@Immutable
data class DayShiftUiModel(
    val id: String,
    val hospitalName: String,
    val hospitalColor: Int,
    val shiftName: String,
    val startTime: LocalTime,
    val isSelected: Boolean = false,
)
