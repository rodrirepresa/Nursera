package com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel

import com.adidas.mvi.Intent
import java.time.LocalDate
import java.time.YearMonth

sealed interface ScheduleIntent : Intent {
    data object Load : ScheduleIntent

    data class SetDisplayedMonth(val month: YearMonth) : ScheduleIntent

    data class SelectDay(val date: LocalDate) : ScheduleIntent

    data object SwitchToWeekView : ScheduleIntent

    data object BackToCalendar : ScheduleIntent

    data class SelectWeek(val weekDays: ImmutableWeekDays) : ScheduleIntent
}

typealias ImmutableWeekDays = kotlinx.collections.immutable.ImmutableList<CalendarDay>
