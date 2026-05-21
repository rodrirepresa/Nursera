package com.rodrirepresa.nursera.feature.schedule.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adidas.mvi.compose.MviContainer
import com.rodrirepresa.nursera.core.ui.NeoBrutalistDayCard
import com.rodrirepresa.nursera.core.ui.NurseraCard
import com.rodrirepresa.nursera.core.ui.NurseraErrorView
import com.rodrirepresa.nursera.core.ui.NurseraHeader
import com.rodrirepresa.nursera.core.ui.NurseraLoadingView
import com.rodrirepresa.nursera.core.ui.darken
import com.rodrirepresa.nursera.feature.schedule.presentation.R
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.CalendarDay
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.DayShiftUiModel
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.MonthData
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.ScheduleIntent
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.ScheduleState
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.ScheduleViewModel
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.TodayStatusUiModel
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.ViewMode
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DAY_HEADERS = listOf("L", "M", "X", "J", "V", "S", "D")
private val CalendarBackground = Color(0xFFFFF8F0)
private val DayBorder = Color(0xFFB8F5C8)
private val TodayBackground = Color(0xFF1A1A1A)
private val OutOfMonthText = Color(0xFFBBBBBB)

// Pager is anchored at the middle page so swiping in both directions works indefinitely
private const val PAGER_CENTER = Int.MAX_VALUE / 2

private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es"))

private fun YearMonth.toTitle(): String = monthTitleFormatter.format(this).replaceFirstChar { it.uppercase() }

@Composable
internal fun ScheduleScreen(viewModel: ScheduleViewModel = hiltViewModel()) {
    MviContainer(
        state = viewModel.state,
        onSideEffect = {},
    ) { state ->
        when (state) {
            is ScheduleState.Loading -> NurseraLoadingView()
            is ScheduleState.Error ->
                NurseraErrorView(
                    message = stringResource(R.string.schedule_error_message),
                )

            is ScheduleState.Loaded ->
                ScheduleLoadedContent(
                    state = state,
                    executeIntent = viewModel::execute,
                )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ScheduleLoadedContent(
    state: ScheduleState.Loaded,
    executeIntent: (ScheduleIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isCalendarView = state.viewMode is ViewMode.Calendar
    val headerIcon = if (isCalendarView) WeekViewIcon else CalendarGridIcon
    val headerIconDescription =
        stringResource(
            if (isCalendarView) {
                R.string.schedule_switch_to_week_description
            } else {
                R.string.schedule_back_to_calendar_description
            },
        )
    val onHeaderIconClick: () -> Unit =
        if (isCalendarView) {
            { executeIntent(ScheduleIntent.SwitchToWeekView) }
        } else {
            { executeIntent(ScheduleIntent.BackToCalendar) }
        }

    SharedTransitionLayout(
        modifier =
            modifier
                .fillMaxSize()
                .background(CalendarBackground),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            NurseraHeader(
                title = stringResource(R.string.schedule_title),
                subtitle = state.currentMonth.toTitle(),
                onIconClick = onHeaderIconClick,
                icon = headerIcon,
                iconContentDescription = headerIconDescription,
            )
            DayOfWeekRow(modifier = Modifier.padding(horizontal = 16.dp))
            AnimatedContent(
                targetState = state.viewMode,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "scheduleViewMode",
                modifier = Modifier.weight(1f),
            ) { viewMode ->
                when (viewMode) {
                    is ViewMode.Calendar ->
                        CalendarView(
                            state = state,
                            executeIntent = executeIntent,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@AnimatedContent,
                        )

                    is ViewMode.Week ->
                        WeekView(
                            state = state,
                            viewMode = viewMode,
                            executeIntent = executeIntent,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@AnimatedContent,
                        )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun CalendarView(
    state: ScheduleState.Loaded,
    executeIntent: (ScheduleIntent) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    // Each pager page maps to state.currentMonth + (page - PAGER_CENTER) months
    val initialMonth = remember { state.currentMonth }
    val pagerState = rememberPagerState(initialPage = PAGER_CENTER) { Int.MAX_VALUE }
    var lastSettledPage by remember { mutableIntStateOf(PAGER_CENTER) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            if (page != lastSettledPage) {
                lastSettledPage = page
                val month = initialMonth.plusMonths((page - PAGER_CENTER).toLong())
                executeIntent(ScheduleIntent.SetDisplayedMonth(month))
            }
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
            modifier = Modifier.weight(1f),
        ) { page ->
            val month = initialMonth.plusMonths((page - PAGER_CENTER).toLong())
            val monthData = state.monthList[month]
            if (monthData == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DayBorder, strokeWidth = 2.dp)
                }
            } else {
                CalendarGrid(
                    month = month,
                    days = monthData.days,
                    onDayClick = { day -> executeIntent(ScheduleIntent.SelectDay(day.date)) },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
        state.todayStatus?.let { status ->
            TodayStatusSection(
                status = status,
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun WeekView(
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

        AnimatedContent(
            targetState = viewMode.selectedDate to viewMode.dayShifts,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "dayShifts",
        ) { (_, shifts) ->
            if (shifts.isEmpty()) {
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
                    items(shifts, key = { it.id }) { shift ->
                        ShiftCard(shift = shift)
                    }
                }
            }
        }
    }
}

@Composable
private fun ShiftCard(
    shift: DayShiftUiModel,
    modifier: Modifier = Modifier,
) {
    NurseraCard(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(end = 4.dp, bottom = 4.dp),
        backgroundColor = Color(shift.hospitalColor),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = shift.hospitalName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = shift.shiftName,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun DayOfWeekRow(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
    ) {
        DAY_HEADERS.forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = OutOfMonthText,
            )
        }
    }
}

private val RowHeight = 72.dp
private val RowVerticalPadding = 3.dp
private val RowSlotHeight = RowHeight + RowVerticalPadding * 2

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun CalendarGrid(
    month: YearMonth,
    days: ImmutableList<CalendarDay>,
    onDayClick: (CalendarDay) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val weeks = remember(days) { days.chunked(7) }

    Column(modifier = modifier.fillMaxSize()) {
        weeks.forEachIndexed { weekIndex, week ->
            with(sharedTransitionScope) {
                WeekRow(
                    week = week,
                    month = month,
                    weekIndex = weekIndex,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onDayClick = onDayClick,
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.WeekRow(
    week: List<CalendarDay>,
    month: YearMonth,
    weekIndex: Int,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onDayClick: (CalendarDay) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(RowSlotHeight)
                .sharedElement(
                    state = rememberSharedContentState("$month-week-$weekIndex"),
                    animatedVisibilityScope = animatedVisibilityScope,
                )
                .padding(vertical = RowVerticalPadding),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        week.forEach { day ->
            DayCell(
                day = day,
                isSelected = false,
                onClick = { if (day.isCurrentMonth) onDayClick(day) },
                modifier =
                    Modifier
                        .weight(1f)
                        .height(RowHeight),
            )
        }
    }
}

@Composable
private fun DayCell(
    day: CalendarDay,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasShifts = day.isCurrentMonth && day.hospitalColors.isNotEmpty()
    val backgroundColor =
        when {
            isSelected -> Color(0xFFFFE082)
            hasShifts -> Color(day.hospitalColors.first())
            else -> null
        }

    if (backgroundColor != null) {
        NeoBrutalistDayCard(
            backgroundColor = backgroundColor,
            modifier = modifier,
            onClick = onClick,
        ) {
            DayCellLabel(day = day, hasShifts = hasShifts)
        }
    } else {
        Box(
            modifier =
                modifier
                    .then(if (day.isCurrentMonth) Modifier.clickable(onClick = onClick) else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            DayCellLabel(day = day, hasShifts = false)
        }
    }
}

@Composable
private fun DayCellLabel(
    day: CalendarDay,
    hasShifts: Boolean,
) {
    if (day.isToday) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(0.55f)
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .background(TodayBackground),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = day.dayOfMonth.toString(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        }
    } else {
        Text(
            text = if (day.isCurrentMonth) day.dayOfMonth.toString() else "",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (hasShifts) FontWeight.SemiBold else FontWeight.Normal,
            color = if (day.isCurrentMonth) Color.Black else OutOfMonthText,
            textAlign = TextAlign.Center,
        )
    }
}

private val vacationMonthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale("es"))

@Composable
private fun TodayStatusSection(
    status: TodayStatusUiModel,
    modifier: Modifier = Modifier,
) {
    when (status) {
        is TodayStatusUiModel.ActiveShift -> ActiveShiftCard(shift = status.shift, modifier = modifier)
        is TodayStatusUiModel.VacationDays -> VacationCard(status = status, modifier = modifier)
    }
}

@Composable
private fun ActiveShiftCard(
    shift: DayShiftUiModel,
    modifier: Modifier = Modifier,
) {
    NurseraCard(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(end = 4.dp, bottom = 4.dp),
        backgroundColor = Color(shift.hospitalColor),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = stringResource(R.string.schedule_next_shift_label),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = shift.hospitalName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = shift.shiftName,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun VacationCard(
    status: TodayStatusUiModel.VacationDays,
    modifier: Modifier = Modifier,
) {
    val rangeText =
        if (status.startDate.month == status.endDate.month) {
            val monthName = vacationMonthFormatter.format(status.startDate).replaceFirstChar { it.uppercase() }
            stringResource(
                R.string.schedule_vacation_range_same_month,
                status.startDate.dayOfMonth,
                status.endDate.dayOfMonth,
                monthName,
            )
        } else {
            val startMonth = vacationMonthFormatter.format(status.startDate).replaceFirstChar { it.uppercase() }
            val endMonth = vacationMonthFormatter.format(status.endDate).replaceFirstChar { it.uppercase() }
            stringResource(
                R.string.schedule_vacation_range_diff_month,
                status.startDate.dayOfMonth,
                startMonth,
                status.endDate.dayOfMonth,
                endMonth,
            )
        }
    Column {
        Text(
            text = "Próximo descanso".uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.padding(start = 20.dp, bottom = 4.dp),
            fontWeight = FontWeight.Bold,
        )

        NurseraCard(
            modifier =
                modifier
                    .fillMaxWidth()
                    .padding(end = 4.dp, bottom = 4.dp),
            backgroundColor = Color(0xFFDAF5F0),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = status.consecutiveDays.toString(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = DayBorder.darken(),
                    fontSize = 42.sp,
                )
                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(
                        text = stringResource(R.string.schedule_vacation_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = DayBorder.darken(),
                    )
                    Text(
                        text = rangeText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = DayBorder.darken(),
                    )
                }
            }
        }
    }
}

// 3×2 grid icon drawn as a vector — mimics Google Calendar's month-view toggle
private val CalendarGridIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "CalendarGrid",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        val strokeColor = androidx.compose.ui.graphics.SolidColor(Color(0xFF1A1A1A))
        val strokeWidth = 2f
        val dotSize = 2.5f
        val cols = listOf(5f, 12f, 19f)
        val rows = listOf(8f, 16f)
        rows.forEach { y ->
            cols.forEach { x ->
                path(fill = strokeColor) {
                    moveTo(x - dotSize / 2, y - dotSize / 2)
                    lineTo(x + dotSize / 2, y - dotSize / 2)
                    lineTo(x + dotSize / 2, y + dotSize / 2)
                    lineTo(x - dotSize / 2, y + dotSize / 2)
                    close()
                }
            }
        }
    }.build()
}

// 3 horizontal lines icon — represents week/list view
private val WeekViewIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "WeekView",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        val fillColor = androidx.compose.ui.graphics.SolidColor(Color(0xFF1A1A1A))
        val lineHeight = 2f
        val lineWidth = 14f
        val startX = 5f
        listOf(7f, 12f, 17f).forEach { y ->
            path(fill = fillColor) {
                moveTo(startX, y - lineHeight / 2)
                lineTo(startX + lineWidth, y - lineHeight / 2)
                lineTo(startX + lineWidth, y + lineHeight / 2)
                lineTo(startX, y + lineHeight / 2)
                close()
            }
        }
    }.build()
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun ScheduleLoadingPreview() {
    NurseraLoadingView()
}

@Preview(showBackground = true)
@Composable
private fun ScheduleErrorPreview() {
    NurseraErrorView(message = "No se pudo cargar el calendario.")
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true, widthDp = 360, heightDp = 700)
@Composable
private fun ScheduleCalendarPreview() {
    val may = YearMonth.of(2026, 5)
    val days =
        persistentListOf(
            CalendarDay(LocalDate.of(2026, 4, 30), 30, false, false),
            CalendarDay(LocalDate.of(2026, 5, 1), 1, true, false),
            CalendarDay(LocalDate.of(2026, 5, 2), 2, true, false, persistentListOf(0xFFDAF5F0.toInt())),
            CalendarDay(LocalDate.of(2026, 5, 3), 3, true, false, persistentListOf(0xFFFDFDF6.toInt())),
            CalendarDay(LocalDate.of(2026, 5, 4), 4, true, false),
            CalendarDay(
                LocalDate.of(2026, 5, 5),
                5,
                true,
                false,
                persistentListOf(0xFFDAF5F0.toInt(), 0xFFFCDFFF.toInt()),
            ),
            CalendarDay(LocalDate.of(2026, 5, 6), 6, true, false),
            CalendarDay(LocalDate.of(2026, 5, 7), 7, true, false, persistentListOf(0xFFFDFDF6.toInt())),
            CalendarDay(LocalDate.of(2026, 5, 8), 8, true, false),
            CalendarDay(LocalDate.of(2026, 5, 9), 9, true, false, persistentListOf(0xFFDAF5F0.toInt())),
            CalendarDay(LocalDate.of(2026, 5, 10), 10, true, false),
            CalendarDay(LocalDate.of(2026, 5, 11), 11, true, false),
            CalendarDay(
                LocalDate.of(2026, 5, 12),
                12,
                true,
                false,
                persistentListOf(0xFFDAF5F0.toInt(), 0xFFFCDFFF.toInt()),
            ),
            CalendarDay(LocalDate.of(2026, 5, 13), 13, true, false),
            CalendarDay(LocalDate.of(2026, 5, 14), 14, true, false, persistentListOf(0xFFFDFDF6.toInt())),
            CalendarDay(LocalDate.of(2026, 5, 15), 15, true, true),
            CalendarDay(LocalDate.of(2026, 5, 16), 16, true, false, persistentListOf(0xFFDAF5F0.toInt())),
            CalendarDay(LocalDate.of(2026, 5, 17), 17, true, false),
            CalendarDay(LocalDate.of(2026, 5, 18), 18, true, false),
            CalendarDay(
                LocalDate.of(2026, 5, 19),
                19,
                true,
                false,
                persistentListOf(0xFFDAF5F0.toInt(), 0xFFFCDFFF.toInt()),
            ),
            CalendarDay(LocalDate.of(2026, 5, 20), 20, true, false),
            CalendarDay(LocalDate.of(2026, 5, 21), 21, true, false, persistentListOf(0xFFFDFDF6.toInt())),
            CalendarDay(LocalDate.of(2026, 5, 22), 22, true, false),
            CalendarDay(LocalDate.of(2026, 5, 23), 23, true, false, persistentListOf(0xFFDAF5F0.toInt())),
            CalendarDay(LocalDate.of(2026, 5, 24), 24, true, false),
            CalendarDay(LocalDate.of(2026, 5, 25), 25, true, false),
            CalendarDay(
                LocalDate.of(2026, 5, 26),
                26,
                true,
                false,
                persistentListOf(0xFFDAF5F0.toInt(), 0xFFFCDFFF.toInt()),
            ),
            CalendarDay(LocalDate.of(2026, 5, 27), 27, true, false),
            CalendarDay(LocalDate.of(2026, 5, 28), 28, true, false, persistentListOf(0xFFFDFDF6.toInt())),
            CalendarDay(LocalDate.of(2026, 5, 29), 29, true, false),
            CalendarDay(LocalDate.of(2026, 5, 30), 30, true, false),
            CalendarDay(LocalDate.of(2026, 5, 31), 31, true, false),
        )
    ScheduleLoadedContent(
        state =
            ScheduleState.Loaded(
                currentMonth = may,
                monthList = persistentHashMapOf(may to MonthData(days = days)),
            ),
        executeIntent = {},
    )
}
