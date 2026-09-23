package com.rodrirepresa.nursera.feature.hospital.presentation.edit.ui

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
import com.rodrirepresa.nursera.core.ui.darken
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.ShiftUiModel
import java.util.UUID

@Composable
internal fun ExistingShiftRow(
    shift: ShiftUiModel,
    backgroundColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFFF6B6B) else backgroundColor,
        animationSpec = tween(durationMillis = 300),
        label = "shiftSelectionColor",
    )
    NurseraCard(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp, end = 4.dp),
        backgroundColor = animatedColor,
        forcePressed = isSelected,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    shift.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = backgroundColor.darken(),
                )
                Text(
                    shift.schedule,
                    style = MaterialTheme.typography.bodySmall,
                    color = backgroundColor.darken(),
                )
            }
            Text(
                shift.hourlyRate,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = backgroundColor.darken(),
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
private fun ExistingShiftRowPreview() {
    ExistingShiftRow(
        shift = ShiftUiModel(id = UUID.randomUUID(), "Morning", "08:00–15:00", "18.50€/h"),
        backgroundColor = Color(0xFFDAF5F0),
        isSelected = false,
        onClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun ExistingShiftRowSelectedPreview() {
    ExistingShiftRow(
        shift = ShiftUiModel(id = UUID.randomUUID(), "Morning", "08:00–15:00", "18.50€/h"),
        backgroundColor = Color(0xFFDAF5F0),
        isSelected = true,
        onClick = {},
    )
}
