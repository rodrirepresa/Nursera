package com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform
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
            return currentState.copy(
                viewMode =
                    weekMode.copy(
                        dayShifts = weekMode.dayShifts.filterNot { it.isSelected }.toPersistentList(),
                    ),
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
        val shift: DayShiftUiModel,
    ) : ViewTransform<ScheduleState, ScheduleSideEffect>() {
        override fun mutate(currentState: ScheduleState): ScheduleState {
            if (currentState !is ScheduleState.Loaded) return currentState
            val weekMode = currentState.viewMode as? ViewMode.Week ?: return currentState
            val updatedShifts =
                (weekMode.dayShifts + shift)
                    .sortedBy { it.startTime }
                    .toPersistentList()
            return currentState.copy(
                viewMode = weekMode.copy(dayShifts = updatedShifts),
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
