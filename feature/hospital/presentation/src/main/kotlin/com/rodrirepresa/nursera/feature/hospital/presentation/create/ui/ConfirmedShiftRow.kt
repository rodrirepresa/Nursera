package com.rodrirepresa.nursera.feature.hospital.presentation.create.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateShiftItem

@Composable
internal fun ConfirmedShiftRow(
    shift: CreateShiftItem,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val animatedColor by animateColorAsState(
        targetValue = if (shift.isSelected) Color(0xFFFF6B6B) else backgroundColor,
        animationSpec = tween(durationMillis = 300),
        label = "shiftSelectionColor",
    )
    NurseraCard(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp, end = 4.dp),
        backgroundColor = animatedColor,
        forcePressed = shift.isSelected,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(shift.form.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text("${shift.form.startTime}–${shift.form.endTime}", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                "${shift.form.hourlyRate}€/h",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

internal fun autofillTime(value: String): String {
    if (value.isBlank() || value.contains(":")) return value
    val hour = value.toIntOrNull() ?: return value
    if (hour < 0 || hour > 23) return value
    return "%02d:00".format(hour)
}

@Preview(showBackground = true)
@Composable
private fun ConfirmedShiftRowPreview() {
    ConfirmedShiftRow(
        shift =
            CreateShiftItem(
                form = ShiftFormUiState(name = "Morning", startTime = "08:00", endTime = "15:00", hourlyRate = "18.5"),
                id = "1",
            ),
        backgroundColor = Color(0xFFDAF5F0),
        onClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun ConfirmedShiftRowSelectedPreview() {
    ConfirmedShiftRow(
        shift =
            CreateShiftItem(
                form = ShiftFormUiState(name = "Morning", startTime = "08:00", endTime = "15:00", hourlyRate = "18.5"),
                isSelected = true,
                id = "1",
            ),
        backgroundColor = Color(0xFFDAF5F0),
        onClick = {},
    )
}
