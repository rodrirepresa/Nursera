package com.rodrirepresa.nursera.feature.profile.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adidas.mvi.compose.MviContainer
import com.rodrirepresa.nursera.core.ui.NurseraCard
import com.rodrirepresa.nursera.core.ui.NurseraErrorView
import com.rodrirepresa.nursera.core.ui.NurseraHeader
import com.rodrirepresa.nursera.core.ui.NurseraIconButton
import com.rodrirepresa.nursera.core.ui.NurseraLoadingView
import com.rodrirepresa.nursera.core.ui.currentLocale
import com.rodrirepresa.nursera.core.ui.darken
import com.rodrirepresa.nursera.feature.profile.presentation.R
import com.rodrirepresa.nursera.feature.profile.presentation.viewmodel.HospitalEarningsUiModel
import com.rodrirepresa.nursera.feature.profile.presentation.viewmodel.ProfileIntent
import com.rodrirepresa.nursera.feature.profile.presentation.viewmodel.ProfileMonthSummaryUiModel
import com.rodrirepresa.nursera.feature.profile.presentation.viewmodel.ProfileState
import com.rodrirepresa.nursera.feature.profile.presentation.viewmodel.ProfileViewModel
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.persistentListOf
import java.text.NumberFormat
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale

private val ScreenBackground = Color(0xFFFFF8F0)
private val BorderColor = Color(0xFF1A1A1A)

private fun monthFormatter(locale: Locale) = DateTimeFormatter.ofPattern("MMMM yyyy", locale)

/**
 * Amounts are always paid in euros, but grouping and symbol placement follow the device locale.
 */
private fun euroFormat(locale: Locale): NumberFormat = NumberFormat.getCurrencyInstance(locale).apply { currency = Currency.getInstance("EUR") }

@Composable
internal fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    MviContainer(
        state = viewModel.state,
        onSideEffect = {},
    ) { state ->
        when (state) {
            is ProfileState.Loading -> NurseraLoadingView()
            is ProfileState.Error ->
                NurseraErrorView(
                    message = stringResource(R.string.profile_error_message),
                )
            is ProfileState.Loaded ->
                ProfileLoadedContent(
                    state = state,
                    executeIntent = viewModel::execute,
                )
        }
    }
}

@Composable
private fun ProfileLoadedContent(
    state: ProfileState.Loaded,
    executeIntent: (ProfileIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentMonthSummary = state.summariesByMonth[state.currentMonth] ?: ProfileMonthSummaryUiModel(month = state.currentMonth)
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(ScreenBackground),
    ) {
        NurseraHeader(
            title = stringResource(R.string.profile_title),
            subtitle = stringResource(R.string.profile_subtitle),
        )
        MonthSelector(
            month = state.currentMonth,
            onPrevious = {
                executeIntent(ProfileIntent.SetDisplayedMonth(state.currentMonth.minusMonths(1)))
            },
            onNext = {
                executeIntent(ProfileIntent.SetDisplayedMonth(state.currentMonth.plusMonths(1)))
            },
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                ProfileChartCard(summary = currentMonthSummary)
            }
            item {
                MonthlyStatsCard(summary = currentMonthSummary)
            }
            item {
                Text(
                    text = stringResource(R.string.profile_breakdown_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            if (currentMonthSummary.hospitals.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.profile_no_data),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF777777),
                    )
                }
            } else {
                items(currentMonthSummary.hospitals, key = { it.hospitalName }) { hospital ->
                    HospitalBreakdownRow(hospital = hospital)
                }
            }
        }
    }
}

@Composable
private fun MonthSelector(
    month: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NurseraIconButton(
            onClick = onPrevious,
            shadowOffset = 3.dp,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.profile_previous_month),
            )
        }
        Text(
            text = month.toUiTitle(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        NurseraIconButton(
            onClick = onNext,
            shadowOffset = 3.dp,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.profile_next_month),
            )
        }
    }
}

@Composable
private fun ProfileChartCard(
    summary: ProfileMonthSummaryUiModel,
    modifier: Modifier = Modifier,
) {
    NurseraCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = Color.White,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = summary.month.toUiTitle(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
            DonutChart(
                hospitals = summary.hospitals,
                totalNetAmount = summary.totalNetAmount,
                totalGrossAmount = summary.totalGrossAmount,
                modifier = Modifier.size(220.dp),
            )
        }
    }
}

@Composable
private fun DonutChart(
    hospitals: List<HospitalEarningsUiModel>,
    totalNetAmount: Double,
    totalGrossAmount: Double,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val borderWidth = 2.5.dp.toPx()
            val shadowOffset = 4.dp.toPx()
            val strokeWidth = size.minDimension * 0.22f
            val diameter = size.minDimension - strokeWidth - shadowOffset - borderWidth * 2f
            val ringTopLeft = Offset((size.width - diameter - shadowOffset) / 2f, (size.height - diameter - shadowOffset) / 2f)

            val chartSlices =
                hospitals
                    .filter { it.percentage > 0f }
                    .ifEmpty {
                        listOf(HospitalEarningsUiModel("", 0xFFE5DCD2.toInt(), 0.0, 0.0, 0f, 100f, 0))
                    }

            val gapAngle = if (chartSlices.size == 1) 0f else 5f
            val availableAngle = 360f - gapAngle * chartSlices.size
            var startAngle = -90f
            chartSlices.forEach { hospital ->
                val sweep = (hospital.percentage / 100f) * availableAngle
                if (sweep > 0f) {
                    drawNeoBrutalistSlice(
                        color = Color(hospital.hospitalColor),
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        topLeft = ringTopLeft,
                        diameter = diameter,
                        strokeWidth = strokeWidth,
                        borderWidth = borderWidth,
                        shadowOffset = shadowOffset,
                    )
                }
                startAngle += sweep + gapAngle
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.profile_total_net_label),
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF666666),
            )
            Text(
                text = euroFormat(currentLocale()).format(totalNetAmount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.profile_total_gross_compact, euroFormat(currentLocale()).format(totalGrossAmount)),
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF666666),
            )
        }
    }
}

private fun DrawScope.drawNeoBrutalistSlice(
    color: Color,
    startAngle: Float,
    sweepAngle: Float,
    topLeft: Offset,
    diameter: Float,
    strokeWidth: Float,
    borderWidth: Float,
    shadowOffset: Float,
) {
    val shadowTopLeft = topLeft + Offset(shadowOffset, shadowOffset)

    drawArc(
        color = color.darken(),
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = shadowTopLeft,
        size = Size(diameter, diameter),
        style = Stroke(width = strokeWidth + borderWidth * 2f, cap = StrokeCap.Butt),
    )
    drawArc(
        color = BorderColor,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = topLeft,
        size = Size(diameter, diameter),
        style = Stroke(width = strokeWidth + borderWidth * 2f, cap = StrokeCap.Butt),
    )
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = topLeft,
        size = Size(diameter, diameter),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
    )
}

@Composable
private fun MonthlyStatsCard(
    summary: ProfileMonthSummaryUiModel,
    modifier: Modifier = Modifier,
) {
    val leadingHospital = summary.hospitals.firstOrNull()
    NurseraCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = Color(0xFFFFEFB6),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.profile_monthly_stats_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = BorderColor,
            )
            Text(
                text = stringResource(R.string.profile_shifts_count, summary.totalShifts),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(R.string.profile_total_net, euroFormat(currentLocale()).format(summary.totalNetAmount)),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.profile_total_gross, euroFormat(currentLocale()).format(summary.totalGrossAmount)),
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text =
                    if (leadingHospital == null) {
                        stringResource(R.string.profile_top_hospital_none)
                    } else {
                        stringResource(R.string.profile_top_hospital, leadingHospital.hospitalName)
                    },
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun HospitalBreakdownRow(
    hospital: HospitalEarningsUiModel,
    modifier: Modifier = Modifier,
) {
    NurseraCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = Color(hospital.hospitalColor),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(hospital.hospitalColor).darken()),
                )
                Column {
                    Text(
                        text = hospital.hospitalName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(hospital.hospitalColor).darken(),
                    )
                    Text(
                        text = stringResource(R.string.profile_shift_count, hospital.shiftsCount),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(hospital.hospitalColor).darken(),
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = euroFormat(currentLocale()).format(hospital.netAmount),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(hospital.hospitalColor).darken(),
                )
                Text(
                    text = stringResource(R.string.profile_percentage, hospital.percentage),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(hospital.hospitalColor).darken(),
                )
                Text(
                    text = stringResource(R.string.profile_gross_and_irpf, euroFormat(currentLocale()).format(hospital.grossAmount), hospital.irpf),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(hospital.hospitalColor).darken(),
                )
            }
        }
    }
}

@Composable
@ReadOnlyComposable
private fun YearMonth.toUiTitle(): String {
    val locale = currentLocale()
    return monthFormatter(locale).format(this).replaceFirstChar { it.uppercase(locale) }
}

@Preview(showBackground = true)
@Composable
private fun ProfileLoadingPreview() {
    NurseraLoadingView()
}

@Preview(showBackground = true)
@Composable
private fun ProfileErrorPreview() {
    NurseraErrorView(message = "Could not load the profile.")
}

@Preview(showBackground = true)
@Composable
private fun ProfileLoadedPreview() {
    val month = YearMonth.now()
    val summary =
        ProfileMonthSummaryUiModel(
            month = month,
            totalGrossAmount = 2272.5,
            totalNetAmount = 1878.18,
            totalShifts = 12,
            hospitals =
                persistentListOf(
                    HospitalEarningsUiModel("Hospital La Paz", 0xFFDAF5F0.toInt(), 980.5, 833.43, 15f, 44.37f, 5),
                    HospitalEarningsUiModel("Hospital Quirón", 0xFFFFCBA4.toInt(), 792.0, 633.6, 20f, 33.73f, 4),
                    HospitalEarningsUiModel("Hospital La Fe", 0xFFFF8FAB.toInt(), 500.0, 411.15, 17.77f, 21.9f, 3),
                ),
        )
    ProfileLoadedContent(
        state =
            ProfileState.Loaded(
                currentMonth = month,
                summariesByMonth = persistentHashMapOf(month to summary),
            ),
        executeIntent = {},
    )
}
