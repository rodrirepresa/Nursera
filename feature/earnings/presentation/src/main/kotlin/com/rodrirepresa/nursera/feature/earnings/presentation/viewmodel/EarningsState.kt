package com.rodrirepresa.nursera.feature.earnings.presentation.viewmodel

import androidx.compose.runtime.Immutable
import com.adidas.mvi.LoggableState
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf
import java.time.YearMonth

sealed interface EarningsState : LoggableState {
    data object Loading : EarningsState

    data object Error : EarningsState

    @Immutable
    data class Loaded(
        val currentMonth: YearMonth,
        val summariesByMonth: PersistentMap<YearMonth, EarningsMonthSummaryUiModel> = persistentHashMapOf(),
    ) : EarningsState
}
