package com.rodrirepresa.nursera.feature.earnings.presentation.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
internal fun EarningsChartCard(
    summary: EarningsMonthSummaryUiModel,
    modifier: Modifier = Modifier,
) {
    // Progress drives both the arc sweep and the counting-up amounts, keeping them in sync.
    // Persisted via rememberSaveable so the count-up plays only once per month, even across
    // recompositions or configuration changes — revisiting a month shows the final value right away.
    val monthKey = summary.month.toString()
    var animatedMonthKeys by rememberSaveable { mutableStateOf("") }
    val alreadyAnimated = remember(monthKey, animatedMonthKeys) { monthKey in animatedMonthKeys.split(",") }
    val progress = remember(monthKey) { Animatable(if (alreadyAnimated) 1f else 0f) }

    LaunchedEffect(monthKey) {
        if (!alreadyAnimated) {
            progress.animateTo(1f, animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing))
            animatedMonthKeys = (animatedMonthKeys.split(",").filter { it.isNotEmpty() } + monthKey).joinToString(",")
        }
    }

    NurseraCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = Color.White,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = summary.month.toUiTitle(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                DonutChart(
                    hospitals = summary.hospitals,
                    progress = progress.value,
                    modifier = Modifier.size(128.dp),
                )
                EarningsSummaryColumn(
                    totalNetAmount = summary.totalNetAmount * progress.value,
                    totalGrossAmount = summary.totalGrossAmount * progress.value,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
internal fun EarningsSummaryColumn(
    totalNetAmount: Double,
    totalGrossAmount: Double,
    modifier: Modifier = Modifier,
) {
    // Placed beside the chart (not on top of it) so the amounts never overlap the arcs.
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = stringResource(R.string.earnings_total_net_label),
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF666666),
        )
        Text(
            text = euroFormat(currentLocale()).format(totalNetAmount),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            text = stringResource(R.string.earnings_total_gross_compact, euroFormat(currentLocale()).format(totalGrossAmount)),
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF666666),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EarningsChartCardPreview() {
    EarningsChartCard(
        summary =
            EarningsMonthSummaryUiModel(
                month = YearMonth.now(),
                totalGrossAmount = 2272.5,
                totalNetAmount = 1878.18,
                totalShifts = 12,
                hospitals =
                    persistentListOf(
                        HospitalEarningsUiModel("Hospital La Paz", 0xFFDAF5F0.toInt(), 980.5, 833.43, 15f, 44.37f, 5),
                        HospitalEarningsUiModel("Hospital Quirón", 0xFFFFCBA4.toInt(), 792.0, 633.6, 20f, 33.73f, 4),
                        HospitalEarningsUiModel("Hospital La Fe", 0xFFFF8FAB.toInt(), 500.0, 411.15, 17.77f, 21.9f, 3),
                    ),
            ),
    )
}
