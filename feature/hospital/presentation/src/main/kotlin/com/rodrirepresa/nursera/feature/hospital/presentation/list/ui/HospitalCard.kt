package com.rodrirepresa.nursera.feature.hospital.presentation.list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.unit.sp
import com.rodrirepresa.nursera.core.ui.NeoBrutalistCard
import com.rodrirepresa.nursera.core.ui.NeoBrutalistChip

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun HospitalCard(
    hospital: HospitalUiModel,
    onHospitalClick: () -> Unit,
) {
    NeoBrutalistCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(hospital.color),
        onClick = onHospitalClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
            ) {
                Text(
                    text = hospital.name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                val label =
                    when (val size = hospital.shifts.size) {
                        0 -> "Sin turnos"
                        else -> "$size tipos de turnos"
                    }

                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                FlowRow(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    NeoBrutalistChip(
                        backgroundColor = Color(0xFFF4D738),
                    ) {
                        Text(
                            text = "IRPF ${hospital.irpf}",
                            fontSize = 7.sp,
                            lineHeight = 14.sp,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
            )
        }
    }
}
