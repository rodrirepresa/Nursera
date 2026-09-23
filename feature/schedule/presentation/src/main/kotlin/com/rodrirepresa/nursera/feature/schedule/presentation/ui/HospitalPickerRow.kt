package com.rodrirepresa.nursera.feature.schedule.presentation.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rodrirepresa.nursera.core.ui.NurseraCard
import com.rodrirepresa.nursera.core.ui.darken
import com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel.HospitalPickerItem
import java.util.UUID

@Composable
internal fun HospitalPickerRow(
    hospital: HospitalPickerItem,
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
        Text(
            text = hospital.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(hospital.color).darken(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HospitalPickerRowPreview() {
    HospitalPickerRow(
        hospital = HospitalPickerItem(id = UUID.randomUUID(), name = "Hospital La Paz", color = 0xFFDAF5F0.toInt()),
        onClick = {},
    )
}
