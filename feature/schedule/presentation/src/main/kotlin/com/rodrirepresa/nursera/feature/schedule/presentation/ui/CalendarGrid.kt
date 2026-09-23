package com.rodrirepresa.nursera.feature.schedule.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.CalendarDay
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate
import java.time.YearMonth

private val RowVerticalPadding = 3.dp
private val RowSlotHeight = RowHeight + RowVerticalPadding * 2

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun CalendarGrid(
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true)
@Composable
private fun CalendarGridPreview() {
    val days =
        persistentListOf(
            CalendarDay(LocalDate.of(2026, 5, 1), 1, true, false),
            CalendarDay(LocalDate.of(2026, 5, 2), 2, true, false, persistentListOf(0xFFDAF5F0.toInt())),
            CalendarDay(LocalDate.of(2026, 5, 3), 3, true, true),
            CalendarDay(LocalDate.of(2026, 5, 4), 4, true, false),
            CalendarDay(LocalDate.of(2026, 5, 5), 5, true, false, persistentListOf(0xFFFCDFFF.toInt())),
            CalendarDay(LocalDate.of(2026, 5, 6), 6, true, false),
            CalendarDay(LocalDate.of(2026, 5, 7), 7, true, false),
        )
    SharedTransitionLayout {
        AnimatedContent(targetState = Unit, label = "calendarGridPreview") {
            CalendarGrid(
                month = YearMonth.of(2026, 5),
                days = days,
                onDayClick = {},
                sharedTransitionScope = this@SharedTransitionLayout,
                animatedVisibilityScope = this@AnimatedContent,
            )
        }
    }
}
