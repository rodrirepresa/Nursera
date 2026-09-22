package com.rodrirepresa.nursera.feature.earnings.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adidas.mvi.compose.MviContainer
import com.rodrirepresa.nursera.core.ui.NurseraErrorView
import com.rodrirepresa.nursera.core.ui.NurseraHeader
import com.rodrirepresa.nursera.core.ui.NurseraLoadingView
import com.rodrirepresa.nursera.feature.earnings.presentation.R
import com.rodrirepresa.nursera.feature.earnings.presentation.viewmodel.EarningsIntent
import com.rodrirepresa.nursera.feature.earnings.presentation.viewmodel.EarningsMonthSummaryUiModel
import com.rodrirepresa.nursera.feature.earnings.presentation.viewmodel.EarningsState
import com.rodrirepresa.nursera.feature.earnings.presentation.viewmodel.EarningsViewModel
import com.rodrirepresa.nursera.feature.earnings.presentation.viewmodel.HospitalEarningsUiModel
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.persistentListOf
import java.time.YearMonth

@Composable
internal fun EarningsScreen(viewModel: EarningsViewModel = hiltViewModel()) {
    MviContainer(
        state = viewModel.state,
        onSideEffect = {},
    ) { state ->
        when (state) {
            is EarningsState.Loading -> NurseraLoadingView()
            is EarningsState.Error ->
                NurseraErrorView(
                    message = stringResource(R.string.earnings_error_message),
                )
            is EarningsState.Loaded ->
                EarningsLoadedContent(
                    state = state,
                    executeIntent = viewModel::execute,
                )
        }
    }
}

@Composable
private fun EarningsLoadedContent(
    state: EarningsState.Loaded,
    executeIntent: (EarningsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentMonthSummary = state.summariesByMonth[state.currentMonth] ?: EarningsMonthSummaryUiModel(month = state.currentMonth)
    val listState = rememberLazyListState()

    // Collapse the month selector while scrolling down into the breakdown to free up space,
    // and expand it again as soon as the user scrolls back up (or reaches the very top).
    var isMonthSelectorExpanded by remember { mutableStateOf(true) }
    LaunchedEffect(listState) {
        var previousIndex = listState.firstVisibleItemIndex
        var previousOffset = listState.firstVisibleItemScrollOffset
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                isMonthSelectorExpanded =
                    when {
                        index == 0 && offset <= 0 -> true
                        index > previousIndex || (index == previousIndex && offset > previousOffset) -> false
                        index < previousIndex || (index == previousIndex && offset < previousOffset) -> true
                        else -> isMonthSelectorExpanded
                    }
                previousIndex = index
                previousOffset = offset
            }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(ScreenBackground),
    ) {
        NurseraHeader(
            title = stringResource(R.string.earnings_title),
            subtitle = stringResource(R.string.earnings_subtitle),
        )
        AnimatedVisibility(
            visible = isMonthSelectorExpanded,
            enter = expandVertically(tween(220)) + fadeIn(tween(220)),
            exit = shrinkVertically(tween(220)) + fadeOut(tween(150)),
        ) {
            Column {
                MonthSelector(
                    month = state.currentMonth,
                    onPrevious = {
                        executeIntent(EarningsIntent.SetDisplayedMonth(state.currentMonth.minusMonths(1)))
                    },
                    onNext = {
                        executeIntent(EarningsIntent.SetDisplayedMonth(state.currentMonth.plusMonths(1)))
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                EarningsChartCard(summary = currentMonthSummary)
            }
            item {
                MonthlyStatsCard(summary = currentMonthSummary)
            }
            item {
                Text(
                    text = stringResource(R.string.earnings_breakdown_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            if (currentMonthSummary.hospitals.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.earnings_no_data),
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

@Preview(showBackground = true)
@Composable
private fun EarningsLoadingPreview() {
    NurseraLoadingView()
}

@Preview(showBackground = true)
@Composable
private fun EarningsErrorPreview() {
    NurseraErrorView(message = "Could not load the earnings summary.")
}

@Preview(showBackground = true)
@Composable
private fun EarningsLoadedPreview() {
    val month = YearMonth.now()
    val summary =
        EarningsMonthSummaryUiModel(
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
    EarningsLoadedContent(
        state =
            EarningsState.Loaded(
                currentMonth = month,
                summariesByMonth = persistentHashMapOf(month to summary),
            ),
        executeIntent = {},
    )
}
