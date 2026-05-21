package com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf
import java.time.LocalDate

@Immutable
data class MonthData(
    val days: ImmutableList<CalendarDay>,
    val shiftsByDay: PersistentMap<LocalDate, ImmutableList<DayShiftUiModel>> = persistentHashMapOf(),
)
