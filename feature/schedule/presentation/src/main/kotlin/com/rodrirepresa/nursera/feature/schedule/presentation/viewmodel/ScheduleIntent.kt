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

    data class ToggleShiftSelection(val shiftId: String) : ScheduleIntent

    data object DeleteSelectedShifts : ScheduleIntent

    data object OpenAddShiftSheet : ScheduleIntent

    data object DismissAddShiftSheet : ScheduleIntent

    data class SelectHospital(val hospitalId: java.util.UUID) : ScheduleIntent

    data class AddShift(
        val hospitalId: java.util.UUID,
        val hospitalName: String,
        val hospitalColor: Int,
        val shiftId: java.util.UUID,
        val shiftName: String,
        val startTime: java.time.LocalTime,
    ) : ScheduleIntent
}

typealias ImmutableWeekDays = kotlinx.collections.immutable.ImmutableList<CalendarDay>
