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
import com.rodrirepresa.nursera.core.ui.NurseraCard
import com.rodrirepresa.nursera.feature.schedule.presentation.R
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.DayShiftUiModel
import java.time.LocalTime

@Composable
internal fun ActiveShiftCard(
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

@Preview(showBackground = true)
@Composable
private fun ActiveShiftCardPreview() {
    ActiveShiftCard(
        shift =
            DayShiftUiModel(
                id = "1",
                hospitalName = "Hospital La Paz",
                hospitalColor = 0xFFDAF5F0.toInt(),
                shiftName = "Morning",
                startTime = LocalTime.of(8, 0),
            ),
    )
}
