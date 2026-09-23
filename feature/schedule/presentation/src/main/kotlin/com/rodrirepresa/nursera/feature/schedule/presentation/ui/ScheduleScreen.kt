package com.rodrirepresa.nursera.feature.schedule.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adidas.mvi.compose.MviContainer
import com.rodrirepresa.nursera.core.ui.NurseraErrorView
import com.rodrirepresa.nursera.core.ui.NurseraHeader
import com.rodrirepresa.nursera.core.ui.NurseraLoadingView
import com.rodrirepresa.nursera.core.ui.currentLocale
import com.rodrirepresa.nursera.feature.schedule.presentation.R
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.CalendarDay
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.MonthData
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.ScheduleIntent
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.ScheduleState
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.ScheduleViewModel
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.ViewMode
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun monthTitleFormatter(locale: Locale) = DateTimeFormatter.ofPattern("MMMM yyyy", locale)

@Composable
@ReadOnlyComposable
private fun YearMonth.toTitle(): String {
    val locale = currentLocale()
    return monthTitleFormatter(locale).format(this).replaceFirstChar { it.uppercase(locale) }
}

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
                // Key only by the view-mode type: without this, any change to fields inside
                // ViewMode.Week (e.g. dayShifts after adding a shift) would be treated as a
                // brand-new target state, tearing down and recreating WeekView (losing its
                // pagerState) instead of just recomposing it with the updated data.
                contentKey = { it::class },
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
