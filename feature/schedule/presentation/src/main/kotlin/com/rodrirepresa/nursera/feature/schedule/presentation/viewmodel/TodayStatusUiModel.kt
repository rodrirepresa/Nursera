package com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel

import androidx.compose.runtime.Immutable
import java.time.LocalDate

@Immutable
sealed interface TodayStatusUiModel {
    @Immutable
    data class ActiveShift(
        val shift: DayShiftUiModel,
    ) : TodayStatusUiModel

    @Immutable
    data class VacationDays(
        val consecutiveDays: Int,
        val startDate: LocalDate,
        val endDate: LocalDate,
    ) : TodayStatusUiModel
}
