package com.rodrirepresa.nursera.feature.schedule.presentation.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rodrirepresa.nursera.core.ui.NurseraCard
import com.rodrirepresa.nursera.core.ui.darken
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.DayShiftUiModel
import java.time.LocalTime

@Composable
internal fun ShiftCard(
    shift: DayShiftUiModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFFF6B6B) else Color(shift.hospitalColor),
        label = "dayShiftCardColor",
    )
    NurseraCard(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(end = 4.dp, bottom = 4.dp),
        backgroundColor = backgroundColor,
        forcePressed = isSelected,
        onClick = onClick,
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
                color = Color(shift.hospitalColor).darken(),
            )
            Text(
                text = shift.shiftName,
                style = MaterialTheme.typography.bodySmall,
                color = Color(shift.hospitalColor).darken(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShiftCardPreview() {
    ShiftCard(
        shift =
            DayShiftUiModel(
                id = "1",
                hospitalName = "Hospital La Paz",
                hospitalColor = 0xFFDAF5F0.toInt(),
                shiftName = "Morning",
                startTime = LocalTime.of(8, 0),
            ),
        isSelected = false,
        onClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun ShiftCardSelectedPreview() {
    ShiftCard(
        shift =
            DayShiftUiModel(
                id = "1",
                hospitalName = "Hospital La Paz",
                hospitalColor = 0xFFDAF5F0.toInt(),
                shiftName = "Morning",
                startTime = LocalTime.of(8, 0),
                isSelected = true,
            ),
        isSelected = true,
        onClick = {},
    )
}
