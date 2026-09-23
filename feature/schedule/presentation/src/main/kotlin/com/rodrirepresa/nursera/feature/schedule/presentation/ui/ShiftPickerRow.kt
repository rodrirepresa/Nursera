package com.rodrirepresa.nursera.feature.schedule.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rodrirepresa.nursera.core.ui.NurseraCard
import com.rodrirepresa.nursera.core.ui.darken
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.HospitalPickerItem
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.ShiftPickerItem
import java.util.UUID

@Composable
internal fun ShiftPickerRow(
    hospital: HospitalPickerItem,
    shift: ShiftPickerItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NurseraCard(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(end = 4.dp, bottom = 4.dp),
        backgroundColor = Color(hospital.color),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = shift.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(hospital.color).darken(),
            )
            Text(
                text = "${shift.startTime}–${shift.endTime}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(hospital.color).darken(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShiftPickerRowPreview() {
    ShiftPickerRow(
        hospital = HospitalPickerItem(id = UUID.randomUUID(), name = "Hospital La Paz", color = 0xFFDAF5F0.toInt()),
        shift = ShiftPickerItem(id = UUID.randomUUID(), name = "Morning", startTime = "08:00", endTime = "15:00"),
        onClick = {},
    )
}
