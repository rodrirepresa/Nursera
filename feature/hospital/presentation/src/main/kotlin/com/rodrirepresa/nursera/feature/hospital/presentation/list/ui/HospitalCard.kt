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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rodrirepresa.nursera.core.ui.NurseraCard
import com.rodrirepresa.nursera.core.ui.NurseraChip
import com.rodrirepresa.nursera.core.ui.darken
import com.rodrirepresa.nursera.feature.hospital.presentation.R
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun HospitalCard(
    hospital: HospitalUiModel,
    onHospitalClick: () -> Unit,
) {
    NurseraCard(
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
                    color = Color(hospital.color).darken(),
                )

                val label =
                    when (val size = hospital.shifts.size) {
                        0 -> stringResource(R.string.hospital_list_no_shifts)
                        else -> pluralStringResource(R.plurals.hospital_list_shift_types, size, size)
                    }

                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(hospital.color).darken(),
                )

                FlowRow(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    NurseraChip(
                        backgroundColor = Color(0xFFF4D738),
                    ) {
                        Text(
                            text = stringResource(R.string.hospital_list_irpf_chip, hospital.irpf),
                            fontSize = 7.sp,
                            lineHeight = 14.sp,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF4D738).darken(),
                        )
                    }
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(hospital.color).darken(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HospitalCardPreview() {
    MaterialTheme {
        HospitalCard(
            hospital =
                HospitalUiModel(
                    id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                    name = "Hospital Central",
                    color = 0xFFE3F2FD.toInt(),
                    irpf = "15%",
                    shifts = emptyList(),
                ),
            onHospitalClick = {},
        )
    }
}
