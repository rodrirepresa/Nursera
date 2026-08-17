package com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import java.time.LocalDate
import java.time.YearMonth

internal object ScheduleTransform {
    data class InitLoaded(
        val currentMonth: YearMonth,
    ) : ViewTransform<ScheduleState, ScheduleSideEffect>() {
        override fun mutate(currentState: ScheduleState): ScheduleState = ScheduleState.Loaded(currentMonth = currentMonth)
    }

    data class AddMonth(
        val month: YearMonth,
        val monthData: MonthData,
    ) : ViewTransform<ScheduleState, ScheduleSideEffect>() {
        override fun mutate(currentState: ScheduleState): ScheduleState {
            if (currentState !is ScheduleState.Loaded) return currentState
            return currentState.copy(monthList = currentState.monthList.put(month, monthData))
        }
    }

    data class SetDisplayedMonth(
        val month: YearMonth,
    ) : ViewTransform<ScheduleState, ScheduleSideEffect>() {
        override fun mutate(currentState: ScheduleState): ScheduleState {
            if (currentState !is ScheduleState.Loaded) return currentState
            return currentState.copy(currentMonth = month)
        }
    }

    data class SetSelectedDate(
        val date: LocalDate,
    ) : ViewTransform<ScheduleState, ScheduleSideEffect>() {
        override fun mutate(currentState: ScheduleState): ScheduleState {
            if (currentState !is ScheduleState.Loaded) return currentState
            return currentState.copy(selectedDate = date)
        }
    }

    data class ShowWeekView(
        val viewMode: ViewMode.Week,
    ) : ViewTransform<ScheduleState, ScheduleSideEffect>() {
        override fun mutate(currentState: ScheduleState): ScheduleState {
            if (currentState !is ScheduleState.Loaded) return currentState
            return currentState.copy(viewMode = viewMode)
        }
    }

    object ShowCalendarView : ViewTransform<ScheduleState, ScheduleSideEffect>() {
        override fun mutate(currentState: ScheduleState): ScheduleState {
            if (currentState !is ScheduleState.Loaded) return currentState
            return currentState.copy(viewMode = ViewMode.Calendar)
        }
    }

    object ShowError : ViewTransform<ScheduleState, ScheduleSideEffect>() {
        override fun mutate(currentState: ScheduleState): ScheduleState = ScheduleState.Error
    }

    data class SetTodayStatus(
        val status: TodayStatusUiModel?,
    ) : ViewTransform<ScheduleState, ScheduleSideEffect>() {
        override fun mutate(currentState: ScheduleState): ScheduleState {
            if (currentState !is ScheduleState.Loaded) return currentState
            return currentState.copy(todayStatus = status)
        }
    }

    data class ToggleShiftSelection(
        val shiftId: String,
    ) : ViewTransform<ScheduleState, ScheduleSideEffect>() {
        override fun mutate(currentState: ScheduleState): ScheduleState {
            if (currentState !is ScheduleState.Loaded) return currentState
            val weekMode = currentState.viewMode as? ViewMode.Week ?: return currentState
            return currentState.copy(
                viewMode =
                    weekMode.copy(
                        dayShifts =
                            weekMode.dayShifts.map { shift ->
                                if (shift.id == shiftId) shift.copy(isSelected = !shift.isSelected) else shift
                            }.toPersistentList(),
                    ),
            )
        }
    }

    object DeleteSelectedShifts : ViewTransform<ScheduleState, ScheduleSideEffect>() {
        override fun mutate(currentState: ScheduleState): ScheduleState {
            if (currentState !is ScheduleState.Loaded) return currentState
            val weekMode = currentState.viewMode as? ViewMode.Week ?: return currentState
            val selectedShiftIds = weekMode.dayShifts.filter { it.isSelected }.mapTo(mutableSetOf()) { it.id }
            if (selectedShiftIds.isEmpty()) return currentState
            val month = YearMonth.from(weekMode.selectedDate)
            val monthData = currentState.monthList[month]
            val updatedMonthList =
                if (monthData == null) {
                    currentState.monthList
                } else {
                    currentState.monthList.put(
                        month,
                        monthData.withRemovedShiftsForDay(weekMode.selectedDate, selectedShiftIds),
                    )
                }
            return currentState.copy(
                viewMode =
                    weekMode.copy(
                        dayShifts = weekMode.dayShifts.filterNot { it.isSelected }.toPersistentList(),
                    ),
                monthList = updatedMonthList,
                todayStatus = computeTodayStatus(LocalDate.now(), updatedMonthList),
            )
        }
    }

    data class OpenAddShiftSheet(
        val sheet: AddShiftSheetUiState,
    ) : ViewTransform<ScheduleState, ScheduleSideEffect>() {
        override fun mutate(currentState: ScheduleState): ScheduleState {
            if (currentState !is ScheduleState.Loaded) return currentState
            return currentState.copy(addShiftSheet = sheet)
        }
    }

    object DismissAddShiftSheet : ViewTransform<ScheduleState, ScheduleSideEffect>() {
        override fun mutate(currentState: ScheduleState): ScheduleState {
            if (currentState !is ScheduleState.Loaded) return currentState
            return currentState.copy(addShiftSheet = null)
        }
    }

    data class AddShiftToDay(
        val date: LocalDate,
        val month: YearMonth,
        val shift: DayShiftUiModel,
    ) : ViewTransform<ScheduleState, ScheduleSideEffect>() {
        override fun mutate(currentState: ScheduleState): ScheduleState {
            if (currentState !is ScheduleState.Loaded) return currentState
            val weekMode = currentState.viewMode as? ViewMode.Week ?: return currentState
            val updatedWeekShifts =
                (weekMode.dayShifts + shift)
                    .sortedBy { it.startTime }
                    .toPersistentList()

            val monthData = currentState.monthList[month]
            val updatedMonthList =
                if (monthData == null) {
                    currentState.monthList
                } else {
                    currentState.monthList.put(
                        month,
                        monthData.withUpdatedShiftsForDay(date, (monthData.shiftsByDay[date] ?: persistentListOf()) + shift),
                    )
                }

            return currentState.copy(
                viewMode = weekMode.copy(dayShifts = updatedWeekShifts),
                monthList = updatedMonthList,
                todayStatus = computeTodayStatus(LocalDate.now(), updatedMonthList),
                addShiftSheet = null,
            )
        }
    }

    data class AddSideEffect(
        val sideEffect: ScheduleSideEffect,
    ) : SideEffectTransform<ScheduleState, ScheduleSideEffect>() {
        override fun mutate(sideEffects: SideEffects<ScheduleSideEffect>): SideEffects<ScheduleSideEffect> = sideEffects.add(sideEffect)
    }
}

private fun MonthData.withUpdatedShiftsForDay(
    date: LocalDate,
    shifts: List<DayShiftUiModel>,
): MonthData {
    val updatedShifts = shifts.sortedBy { it.startTime }.toPersistentList()
    val updatedColors = updatedShifts.map { it.hospitalColor }.distinct().toPersistentList()
    val updatedDays =
        days.map { day ->
            if (day.date == date) {
                day.copy(hospitalColors = updatedColors)
            } else {
                day
            }
        }.toPersistentList()
    return copy(
        days = updatedDays,
        shiftsByDay = shiftsByDay.put(date, updatedShifts),
    )
}

private fun MonthData.withRemovedShiftsForDay(
    date: LocalDate,
    shiftIds: Set<String>,
): MonthData {
    val remainingShifts = (shiftsByDay[date] ?: persistentListOf()).filterNot { it.id in shiftIds }
    return withUpdatedShiftsForDay(date, remainingShifts)
}

private fun computeTodayStatus(
    today: LocalDate,
    monthMap: Map<YearMonth, MonthData>,
): TodayStatusUiModel? {
    val todayMonth = YearMonth.from(today)
    val todayShifts = monthMap[todayMonth]?.shiftsByDay?.get(today)
    if (!todayShifts.isNullOrEmpty()) {
        return TodayStatusUiModel.ActiveShift(todayShifts.first())
    }

    var cursor = today
    var count = 0
    while (true) {
        val cursorMonth = YearMonth.from(cursor)
        val shifts = monthMap[cursorMonth]?.shiftsByDay?.get(cursor)
        if (!shifts.isNullOrEmpty()) break
        count++
        cursor = cursor.plusDays(1)
        if (count > 62) break
    }
    if (count == 0) return null
    return TodayStatusUiModel.VacationDays(
        consecutiveDays = count,
        startDate = today,
        endDate = cursor.minusDays(1),
    )
}
