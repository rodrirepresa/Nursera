package com.rodrirepresa.nursera.feature.schedule.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.CalendarDay
import java.time.LocalDate

private val TodayBackground = Color(0xFF1A1A1A)

@Composable
internal fun DayCellLabel(
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

@Preview(showBackground = true)
@Composable
private fun DayCellLabelTodayPreview() {
    DayCellLabel(
        day = CalendarDay(date = LocalDate.now(), dayOfMonth = 15, isCurrentMonth = true, isToday = true),
        hasShifts = false,
    )
}

@Preview(showBackground = true)
@Composable
private fun DayCellLabelRegularPreview() {
    DayCellLabel(
        day = CalendarDay(date = LocalDate.now(), dayOfMonth = 10, isCurrentMonth = true, isToday = false),
        hasShifts = true,
    )
}
