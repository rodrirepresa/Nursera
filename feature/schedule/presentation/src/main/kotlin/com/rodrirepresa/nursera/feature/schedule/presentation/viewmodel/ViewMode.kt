package com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import java.time.LocalDate

sealed interface ViewMode {
    data object Calendar : ViewMode

    @Immutable
    data class Week(
        val selectedDate: LocalDate,
        val weekIndex: Int,
        val weekDays: ImmutableList<CalendarDay>,
        val dayShifts: ImmutableList<DayShiftUiModel>,
    ) : ViewMode
}
