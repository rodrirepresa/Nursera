package com.rodrirepresa.nursera.feature.schedule.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.DayShiftUiModel
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.TodayStatusUiModel
import java.time.LocalDate
import java.time.LocalTime

@Composable
internal fun TodayStatusSection(
    status: TodayStatusUiModel,
    modifier: Modifier = Modifier,
) {
    when (status) {
        is TodayStatusUiModel.ActiveShift -> ActiveShiftCard(shift = status.shift, modifier = modifier)
        is TodayStatusUiModel.VacationDays -> VacationCard(status = status, modifier = modifier)
    }
}

@Preview(showBackground = true)
@Composable
private fun TodayStatusSectionActiveShiftPreview() {
    TodayStatusSection(
        status =
            TodayStatusUiModel.ActiveShift(
                shift =
                    DayShiftUiModel(
                        id = "1",
                        hospitalName = "Hospital La Paz",
                        hospitalColor = 0xFFDAF5F0.toInt(),
                        shiftName = "Morning",
                        startTime = LocalTime.of(8, 0),
                    ),
            ),
    )
}

@Preview(showBackground = true)
@Composable
private fun TodayStatusSectionVacationPreview() {
    TodayStatusSection(
        status =
            TodayStatusUiModel.VacationDays(
                consecutiveDays = 3,
                startDate = LocalDate.of(2026, 5, 20),
                endDate = LocalDate.of(2026, 5, 22),
            ),
    )
}
