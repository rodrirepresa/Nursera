package com.rodrirepresa.nursera.feature.schedule.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rodrirepresa.nursera.core.ui.NurseraCard
import com.rodrirepresa.nursera.core.ui.currentLocale
import com.rodrirepresa.nursera.core.ui.darken
import com.rodrirepresa.nursera.feature.schedule.presentation.R
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.TodayStatusUiModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun vacationMonthFormatter(locale: Locale) = DateTimeFormatter.ofPattern("MMMM", locale)

@Composable
internal fun VacationCard(
    status: TodayStatusUiModel.VacationDays,
    modifier: Modifier = Modifier,
) {
    val locale = currentLocale()
    val formatter = vacationMonthFormatter(locale)
    val rangeText =
        if (status.startDate.month == status.endDate.month) {
            val monthName = formatter.format(status.startDate).replaceFirstChar { it.uppercase(locale) }
            stringResource(
                R.string.schedule_vacation_range_same_month,
                status.startDate.dayOfMonth,
                status.endDate.dayOfMonth,
                monthName,
            )
        } else {
            val startMonth = formatter.format(status.startDate).replaceFirstChar { it.uppercase(locale) }
            val endMonth = formatter.format(status.endDate).replaceFirstChar { it.uppercase(locale) }
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
            text = stringResource(R.string.schedule_next_break_label).uppercase(),
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

@Preview(showBackground = true)
@Composable
private fun VacationCardPreview() {
    VacationCard(
        status =
            TodayStatusUiModel.VacationDays(
                consecutiveDays = 3,
                startDate = LocalDate.of(2026, 5, 20),
                endDate = LocalDate.of(2026, 5, 22),
            ),
    )
}
