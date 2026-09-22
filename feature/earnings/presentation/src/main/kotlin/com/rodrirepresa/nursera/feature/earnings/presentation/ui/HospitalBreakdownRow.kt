package com.rodrirepresa.nursera.feature.earnings.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.rodrirepresa.nursera.core.ui.NurseraCard
import com.rodrirepresa.nursera.core.ui.currentLocale
import com.rodrirepresa.nursera.core.ui.darken
import com.rodrirepresa.nursera.feature.earnings.presentation.R
import com.rodrirepresa.nursera.feature.earnings.presentation.viewmodel.HospitalEarningsUiModel

@Composable
internal fun HospitalBreakdownRow(
    hospital: HospitalEarningsUiModel,
    modifier: Modifier = Modifier,
) {
    // Rendered directly (no per-row entrance animation) so the LazyColumn scrolls smoothly
    // instead of stuttering while rows animate in.
    NurseraCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = Color(hospital.hospitalColor),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = hospital.hospitalName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(hospital.hospitalColor).darken(),
                )
                Text(
                    text = stringResource(R.string.earnings_shift_count, hospital.shiftsCount),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(hospital.hospitalColor).darken(),
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = euroFormat(currentLocale()).format(hospital.netAmount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(hospital.hospitalColor).darken(),
                )
                Text(
                    text =
                        stringResource(
                            R.string.earnings_total_gross_compact,
                            euroFormat(currentLocale()).format(hospital.grossAmount),
                        ),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(hospital.hospitalColor).darken(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HospitalBreakdownRowPreview() {
    HospitalBreakdownRow(
        hospital = HospitalEarningsUiModel("Hospital La Paz", 0xFFDAF5F0.toInt(), 980.5, 833.43, 15f, 44.37f, 5),
    )
}
