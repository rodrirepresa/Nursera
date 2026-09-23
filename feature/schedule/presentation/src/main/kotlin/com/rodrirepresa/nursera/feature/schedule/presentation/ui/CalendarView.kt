package com.rodrirepresa.nursera.feature.schedule.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.CalendarDay
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.MonthData
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.ScheduleIntent
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.ScheduleState
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate
import java.time.YearMonth

// Pager is anchored at the middle page so swiping in both directions works indefinitely
private const val PAGER_CENTER = Int.MAX_VALUE / 2

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun CalendarView(
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
@Preview(showBackground = true, widthDp = 360, heightDp = 700)
@Composable
private fun CalendarViewPreview() {
    val may = YearMonth.of(2026, 5)
    val days =
        persistentListOf(
            CalendarDay(LocalDate.of(2026, 5, 1), 1, true, false),
            CalendarDay(LocalDate.of(2026, 5, 2), 2, true, false, persistentListOf(0xFFDAF5F0.toInt())),
            CalendarDay(LocalDate.of(2026, 5, 3), 3, true, true),
        )
    SharedTransitionLayout {
        AnimatedContent(targetState = Unit, label = "calendarViewPreview") {
            CalendarView(
                state =
                    ScheduleState.Loaded(
                        currentMonth = may,
                        monthList = persistentHashMapOf(may to MonthData(days = days)),
                    ),
                executeIntent = {},
                sharedTransitionScope = this@SharedTransitionLayout,
                animatedVisibilityScope = this@AnimatedContent,
            )
        }
    }
}
