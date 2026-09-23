package com.rodrirepresa.nursera.feature.schedule.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rodrirepresa.nursera.core.ui.NurseraIconButton
import com.rodrirepresa.nursera.feature.schedule.presentation.R
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.CalendarDay
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.DayShiftUiModel
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.MonthData
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.ScheduleIntent
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.ScheduleState
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.ViewMode
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun WeekView(
    state: ScheduleState.Loaded,
    viewMode: ViewMode.Week,
    executeIntent: (ScheduleIntent) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val month = YearMonth.from(viewMode.selectedDate)
    val monthData = state.monthList[month]
    val allWeeks = remember(monthData) { monthData?.days?.chunked(7) ?: emptyList() }
    val pagerState = rememberPagerState(initialPage = viewMode.weekIndex) { allWeeks.size }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val hasSelectedShifts = viewMode.dayShifts.any { it.isSelected }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            val week = allWeeks.getOrNull(page) ?: return@collect
            executeIntent(ScheduleIntent.SelectWeek(week.toPersistentList()))
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            val week = allWeeks.getOrNull(page) ?: return@HorizontalPager
            with(sharedTransitionScope) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .sharedElement(
                                state = rememberSharedContentState("$month-week-$page"),
                                animatedVisibilityScope = animatedVisibilityScope,
                            )
                            .padding(horizontal = 16.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    week.forEach { day ->
                        DayCell(
                            day = day,
                            isSelected = day.date == viewMode.selectedDate,
                            onClick = { if (day.isCurrentMonth) executeIntent(ScheduleIntent.SelectDay(day.date)) },
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .height(RowHeight),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.schedule_day_shifts_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AnimatedVisibility(
                    visible = hasSelectedShifts,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    NurseraIconButton(
                        onClick = { executeIntent(ScheduleIntent.DeleteSelectedShifts) },
                        backgroundColor = Color(0xFFFF6B6B),
                        shadowOffset = 3.dp,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.schedule_delete_selected_shifts_description),
                            modifier = Modifier.padding(0.dp),
                        )
                    }
                }
                NurseraIconButton(
                    onClick = { executeIntent(ScheduleIntent.OpenAddShiftSheet) },
                    shadowOffset = 3.dp,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.schedule_add_shift_description),
                        modifier = Modifier.padding(0.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (viewMode.dayShifts.isEmpty()) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.schedule_no_shifts_for_day),
                    style = MaterialTheme.typography.bodyMedium,
                    color = OutOfMonthText,
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                items(viewMode.dayShifts, key = { it.id }) { shift ->
                    ShiftCard(
                        shift = shift,
                        isSelected = shift.isSelected,
                        modifier = Modifier.animateItem(),
                        onClick = { executeIntent(ScheduleIntent.ToggleShiftSelection(shift.id)) },
                    )
                }
            }
        }
    }

    if (state.addShiftSheet != null) {
        AddShiftBottomSheet(
            sheet = state.addShiftSheet,
            sheetState = sheetState,
            executeIntent = executeIntent,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true, widthDp = 360, heightDp = 700)
@Composable
private fun WeekViewPreview() {
    val may = YearMonth.of(2026, 5)
    val selectedDate = LocalDate.of(2026, 5, 4)
    val week =
        (1..7).map { dayOfMonth ->
            CalendarDay(
                date = LocalDate.of(2026, 5, dayOfMonth),
                dayOfMonth = dayOfMonth,
                isCurrentMonth = true,
                isToday = dayOfMonth == 4,
            )
        }
    val dayShifts =
        persistentListOf(
            DayShiftUiModel(
                id = "1",
                hospitalName = "Hospital La Paz",
                hospitalColor = 0xFFDAF5F0.toInt(),
                shiftName = "Morning",
                startTime = LocalTime.of(8, 0),
            ),
        )
    SharedTransitionLayout {
        AnimatedContent(targetState = Unit, label = "weekViewPreview") {
            WeekView(
                state =
                    ScheduleState.Loaded(
                        currentMonth = may,
                        monthList = persistentHashMapOf(may to MonthData(days = week.toPersistentList())),
                    ),
                viewMode =
                    ViewMode.Week(
                        selectedDate = selectedDate,
                        weekIndex = 0,
                        weekDays = week.toPersistentList(),
                        dayShifts = dayShifts,
                    ),
                executeIntent = {},
                sharedTransitionScope = this@SharedTransitionLayout,
                animatedVisibilityScope = this@AnimatedContent,
            )
        }
    }
}
