package com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel

import androidx.compose.runtime.Immutable
import com.adidas.mvi.LoggableState
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf
import java.time.LocalDate
import java.time.YearMonth

sealed interface ScheduleState : LoggableState {
    data object Loading : ScheduleState

    data object Error : ScheduleState

    @Immutable
    data class Loaded(
        val currentMonth: YearMonth,
        val selectedDate: LocalDate = LocalDate.now(),
        val monthList: PersistentMap<YearMonth, MonthData> = persistentHashMapOf(),
        val viewMode: ViewMode = ViewMode.Calendar,
        val todayStatus: TodayStatusUiModel? = null,
    ) : ScheduleState
}
