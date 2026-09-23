package com.rodrirepresa.nursera.feature.schedule.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.rodrirepresa.nursera.core.ui.NeoBrutalistDayCard
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.CalendarDay
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate

@Composable
internal fun DayCell(
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

@Preview(showBackground = true)
@Composable
private fun DayCellWithShiftsPreview() {
    DayCell(
        day =
            CalendarDay(
                date = LocalDate.now(),
                dayOfMonth = 12,
                isCurrentMonth = true,
                isToday = false,
                hospitalColors = persistentListOf(0xFFDAF5F0.toInt()),
            ),
        isSelected = false,
        onClick = {},
        modifier = Modifier.height(RowHeight),
    )
}

@Preview(showBackground = true)
@Composable
private fun DayCellEmptyPreview() {
    DayCell(
        day = CalendarDay(date = LocalDate.now(), dayOfMonth = 3, isCurrentMonth = true, isToday = false),
        isSelected = false,
        onClick = {},
        modifier = Modifier.height(RowHeight),
    )
}
