package com.rodrirepresa.nursera.feature.hospital.presentation.list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rodrirepresa.nursera.feature.hospital.presentation.NeoBrutalistCard
import com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel.HospitalIntent

@Composable
internal fun AddHospitalButton(executeIntent: (HospitalIntent) -> Unit) {
    NeoBrutalistCard(
        modifier = Modifier.height(56.dp),
        backgroundColor = Color(0xFFFF6B6B),
        onClick = { executeIntent(HospitalIntent.OpenCreateHospital) },
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add shift",
                modifier = Modifier.padding(end = 4.dp),
            )
            Text(
                text = "Añadir Hospital",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
