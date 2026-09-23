package com.rodrirepresa.nursera.feature.schedule.presentation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val DAY_HEADERS = listOf("L", "M", "X", "J", "V", "S", "D")

@Composable
internal fun DayOfWeekRow(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
    ) {
        DAY_HEADERS.forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = OutOfMonthText,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DayOfWeekRowPreview() {
    DayOfWeekRow(modifier = Modifier.padding(horizontal = 16.dp))
}
