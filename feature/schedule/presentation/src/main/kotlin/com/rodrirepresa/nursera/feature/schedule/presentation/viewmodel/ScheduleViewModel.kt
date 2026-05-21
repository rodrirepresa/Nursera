package com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.transform.StateTransform
import com.rodrirepresa.nursera.core.common.DispatcherProvider
import com.rodrirepresa.nursera.feature.schedule.domain.model.ScheduledShift
import com.rodrirepresa.nursera.feature.schedule.domain.usecase.ObserveMonthScheduleUseCase
import com.rodrirepresa.nursera.feature.schedule.presentation.mappers.toDayShiftUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
internal class ScheduleViewModel
    @Inject
    constructor(
        dispatcherProvider: DispatcherProvider,
        private val observeMonthScheduleUseCase: ObserveMonthScheduleUseCase,
    ) : ViewModel(), MviHost<ScheduleIntent, State<ScheduleState, ScheduleSideEffect>> {
        private val reducer: Reducer<ScheduleIntent, State<ScheduleState, ScheduleSideEffect>> =
            com.adidas.mvi.reducer.Reducer(
                coroutineScope = viewModelScope,
                defaultDispatcher = dispatcherProvider.default(),
                initialInnerState = ScheduleState.Loading,
                intentExecutor = this::executeIntent,
            )

        override val state: StateFlow<State<ScheduleState, ScheduleSideEffect>> = reducer.state

        init {
            execute(ScheduleIntent.Load)
        }

        override fun execute(intent: ScheduleIntent) {
            reducer.executeIntent(intent)
        }

        private fun executeIntent(intent: ScheduleIntent): Flow<StateTransform<State<ScheduleState, ScheduleSideEffect>>> =
            when (intent) {
                is ScheduleIntent.Load -> executeLoad()
                is ScheduleIntent.SetDisplayedMonth -> executeSetDisplayedMonth(intent.month)
                is ScheduleIntent.SelectDay -> executeSelectDay(intent.date)
                is ScheduleIntent.SwitchToWeekView -> executeSwitchToWeekView()
                is ScheduleIntent.BackToCalendar -> executeBackToCalendar()
                is ScheduleIntent.SelectWeek -> executeSelectWeek(intent.weekDays)
            }

        private fun executeLoad(): Flow<StateTransform<State<ScheduleState, ScheduleSideEffect>>> =
            flow {
                val today = YearMonth.now()
                emit(ScheduleTransform.InitLoaded(currentMonth = today))
                val monthMap = mutableMapOf<YearMonth, MonthData>()
                listOf(today.minusMonths(1), today, today.plusMonths(1)).forEach { month ->
                    val data = buildMonthData(month, observeMonthScheduleUseCase(month).first())
                    monthMap[month] = data
                    emit(ScheduleTransform.AddMonth(month = month, monthData = data))
                }
                emit(ScheduleTransform.SetTodayStatus(computeTodayStatus(LocalDate.now(), monthMap)))
            }

        private fun executeSetDisplayedMonth(month: YearMonth): Flow<StateTransform<State<ScheduleState, ScheduleSideEffect>>> =
            flow {
                emit(ScheduleTransform.SetDisplayedMonth(month))
                val cached = (state.value.view as? ScheduleState.Loaded)?.monthList ?: persistentHashMapOf()
                listOf(month.minusMonths(1), month.plusMonths(1)).forEach { adjacent ->
                    if (!cached.containsKey(adjacent)) {
                        val data = buildMonthData(adjacent, observeMonthScheduleUseCase(adjacent).first())
                        emit(ScheduleTransform.AddMonth(month = adjacent, monthData = data))
                    }
                }
            }

        private fun executeSwitchToWeekView(): Flow<StateTransform<State<ScheduleState, ScheduleSideEffect>>> =
            flow {
                val loaded = state.value.view as? ScheduleState.Loaded ?: return@flow
                val date = loaded.selectedDate
                val monthData = loaded.monthList[YearMonth.from(date)] ?: return@flow
                val weeks = monthData.days.chunked(7)
                val weekIndex = weeks.indexOfFirst { week -> week.any { it.date == date } }
                if (weekIndex == -1) return@flow
                emit(
                    ScheduleTransform.ShowWeekView(
                        ViewMode.Week(
                            selectedDate = date,
                            weekIndex = weekIndex,
                            weekDays = weeks[weekIndex].toPersistentList(),
                            dayShifts = monthData.shiftsByDay[date] ?: persistentListOf(),
                        ),
                    ),
                )
            }

        private fun executeSelectDay(date: LocalDate): Flow<StateTransform<State<ScheduleState, ScheduleSideEffect>>> =
            flow {
                val loaded = state.value.view as? ScheduleState.Loaded ?: return@flow
                val monthData = loaded.monthList[YearMonth.from(date)] ?: return@flow
                val weeks = monthData.days.chunked(7)
                val weekIndex = weeks.indexOfFirst { week -> week.any { it.date == date } }
                if (weekIndex == -1) return@flow
                emit(ScheduleTransform.SetSelectedDate(date))
                emit(
                    ScheduleTransform.ShowWeekView(
                        ViewMode.Week(
                            selectedDate = date,
                            weekIndex = weekIndex,
                            weekDays = weeks[weekIndex].toPersistentList(),
                            dayShifts = monthData.shiftsByDay[date] ?: persistentListOf(),
                        ),
                    ),
                )
            }

        private fun executeSelectWeek(weekDays: ImmutableList<CalendarDay>): Flow<StateTransform<State<ScheduleState, ScheduleSideEffect>>> =
            flow {
                val loaded = state.value.view as? ScheduleState.Loaded ?: return@flow
                val weekMode = loaded.viewMode as? ViewMode.Week ?: return@flow
                val selectedDate =
                    if (weekDays.any { it.date == weekMode.selectedDate }) {
                        weekMode.selectedDate
                    } else {
                        weekDays.firstOrNull { it.isCurrentMonth }?.date ?: weekDays.first().date
                    }
                val monthData = loaded.monthList[YearMonth.from(selectedDate)] ?: return@flow
                val weeks = monthData.days.chunked(7)
                val weekIndex = weeks.indexOfFirst { week -> week.any { it.date == weekDays.first().date } }
                emit(
                    ScheduleTransform.ShowWeekView(
                        weekMode.copy(
                            selectedDate = selectedDate,
                            weekIndex = if (weekIndex == -1) weekMode.weekIndex else weekIndex,
                            weekDays = weekDays,
                            dayShifts = monthData.shiftsByDay[selectedDate] ?: persistentListOf(),
                        ),
                    ),
                )
            }

        private fun executeBackToCalendar(): Flow<StateTransform<State<ScheduleState, ScheduleSideEffect>>> =
            flow { emit(ScheduleTransform.ShowCalendarView) }

        private fun computeTodayStatus(
            today: LocalDate,
            monthMap: Map<YearMonth, MonthData>,
        ): TodayStatusUiModel? {
            val todayMonth = YearMonth.from(today)
            val todayShifts = monthMap[todayMonth]?.shiftsByDay?.get(today)
            if (!todayShifts.isNullOrEmpty()) {
                // No time data on shifts yet — show the first shift of today as the active one
                return TodayStatusUiModel.ActiveShift(todayShifts.first())
            }
            // Count consecutive days off starting from today
            var cursor = today
            var count = 0
            while (true) {
                val cursorMonth = YearMonth.from(cursor)
                val shifts = monthMap[cursorMonth]?.shiftsByDay?.get(cursor)
                if (!shifts.isNullOrEmpty()) break
                count++
                cursor = cursor.plusDays(1)
                // Stop looking past what we've loaded
                if (count > 62) break
            }
            if (count == 0) return null
            return TodayStatusUiModel.VacationDays(
                consecutiveDays = count,
                startDate = today,
                endDate = cursor.minusDays(1),
            )
        }

        private fun buildMonthData(
            month: YearMonth,
            shifts: List<ScheduledShift>,
        ): MonthData {
            val days = buildMonthDays(month, shifts).toPersistentList()
            val shiftsByDay =
                shifts
                    .groupBy { it.date }
                    .entries
                    .fold(persistentHashMapOf<LocalDate, ImmutableList<DayShiftUiModel>>()) { map, (date, dayShifts) ->
                        map.put(date, dayShifts.map { it.toDayShiftUiModel() }.toPersistentList())
                    }
            return MonthData(days = days, shiftsByDay = shiftsByDay)
        }

        private fun buildMonthDays(
            month: YearMonth,
            shifts: List<ScheduledShift>,
        ): List<CalendarDay> {
            val today = LocalDate.now()
            val shiftsByDayNum = shifts.groupBy { it.date.dayOfMonth }
            val firstDay = month.atDay(1)
            val leadingBlanks = (firstDay.dayOfWeek.value - 1 + 7) % 7
            val daysInMonth = month.lengthOfMonth()
            val prevMonth = month.minusMonths(1)
            val daysInPrev = prevMonth.lengthOfMonth()
            return buildList {
                repeat(leadingBlanks) { i ->
                    val d = prevMonth.atDay(daysInPrev - leadingBlanks + 1 + i)
                    add(CalendarDay(date = d, dayOfMonth = d.dayOfMonth, isCurrentMonth = false, isToday = false))
                }
                repeat(daysInMonth) { i ->
                    val dayNum = i + 1
                    val d = month.atDay(dayNum)
                    val colors =
                        shiftsByDayNum[dayNum]
                            ?.map { it.hospitalColor }
                            ?.distinct()
                            ?.toPersistentList()
                            ?: persistentListOf()
                    add(
                        CalendarDay(
                            date = d,
                            dayOfMonth = dayNum,
                            isCurrentMonth = true,
                            isToday = d == today,
                            hospitalColors = colors,
                        ),
                    )
                }
                val trailingCount = (7 - size % 7) % 7
                repeat(trailingCount) { i ->
                    val d = month.plusMonths(1).atDay(i + 1)
                    add(CalendarDay(date = d, dayOfMonth = d.dayOfMonth, isCurrentMonth = false, isToday = false))
                }
            }.chunked(7)
                .filter { week -> week.any { it.isCurrentMonth } }
                .flatten()
        }
    }
