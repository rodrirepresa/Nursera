package com.rodrirepresa.nursera.feature.profile.presentation.viewmodel

import androidx.compose.runtime.Immutable
import com.adidas.mvi.LoggableState
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf
import java.time.YearMonth

sealed interface ProfileState : LoggableState {
    data object Loading : ProfileState

    data object Error : ProfileState

    @Immutable
    data class Loaded(
        val currentMonth: YearMonth,
        val summariesByMonth: PersistentMap<YearMonth, ProfileMonthSummaryUiModel> = persistentHashMapOf(),
    ) : ProfileState
}
