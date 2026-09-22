package com.rodrirepresa.nursera.feature.earnings.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rodrirepresa.nursera.core.ui.NurseraCard
import com.rodrirepresa.nursera.core.ui.currentLocale
import com.rodrirepresa.nursera.feature.earnings.presentation.R
import com.rodrirepresa.nursera.feature.earnings.presentation.viewmodel.EarningsMonthSummaryUiModel
import com.rodrirepresa.nursera.feature.earnings.presentation.viewmodel.HospitalEarningsUiModel
import kotlinx.collections.immutable.persistentListOf
import java.time.YearMonth

@Composable
internal fun MonthlyStatsCard(
    summary: EarningsMonthSummaryUiModel,
    modifier: Modifier = Modifier,
) {
    val leadingHospital = summary.hospitals.firstOrNull()
    NurseraCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = Color(0xFFFFEFB6),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.earnings_monthly_stats_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = BorderColor,
            )
            Text(
                text = stringResource(R.string.earnings_shifts_count, summary.totalShifts),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(R.string.earnings_total_net, euroFormat(currentLocale()).format(summary.totalNetAmount)),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.earnings_total_gross, euroFormat(currentLocale()).format(summary.totalGrossAmount)),
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text =
                    if (leadingHospital == null) {
                        stringResource(R.string.earnings_top_hospital_none)
                    } else {
                        stringResource(R.string.earnings_top_hospital, leadingHospital.hospitalName)
                    },
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MonthlyStatsCardPreview() {
    MonthlyStatsCard(
        summary =
            EarningsMonthSummaryUiModel(
                month = YearMonth.now(),
                totalGrossAmount = 2272.5,
                totalNetAmount = 1878.18,
                totalShifts = 12,
                hospitals =
                    persistentListOf(
                        HospitalEarningsUiModel("Hospital La Paz", 0xFFDAF5F0.toInt(), 980.5, 833.43, 15f, 44.37f, 5),
                    ),
            ),
    )
}

@Preview(showBackground = true)
@Composable
private fun MonthlyStatsCardNoDataPreview() {
    MonthlyStatsCard(summary = EarningsMonthSummaryUiModel(month = YearMonth.now()))
}
