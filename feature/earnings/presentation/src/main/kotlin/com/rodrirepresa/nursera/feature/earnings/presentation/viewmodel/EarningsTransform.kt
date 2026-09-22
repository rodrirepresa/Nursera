package com.rodrirepresa.nursera.feature.earnings.presentation.viewmodel

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform
import java.time.YearMonth

internal object EarningsTransform {
    data class InitLoaded(
        val month: YearMonth,
    ) : ViewTransform<EarningsState, EarningsSideEffect>() {
        override fun mutate(currentState: EarningsState): EarningsState = EarningsState.Loaded(currentMonth = month)
    }

    data class SetDisplayedMonth(
        val month: YearMonth,
    ) : ViewTransform<EarningsState, EarningsSideEffect>() {
        override fun mutate(currentState: EarningsState): EarningsState {
            if (currentState !is EarningsState.Loaded) return currentState
            return currentState.copy(currentMonth = month)
        }
    }

    data class UpsertMonthSummary(
        val month: YearMonth,
        val summary: EarningsMonthSummaryUiModel,
    ) : ViewTransform<EarningsState, EarningsSideEffect>() {
        override fun mutate(currentState: EarningsState): EarningsState {
            if (currentState !is EarningsState.Loaded) return currentState
            return currentState.copy(summariesByMonth = currentState.summariesByMonth.put(month, summary))
        }
    }

    object ShowError : ViewTransform<EarningsState, EarningsSideEffect>() {
        override fun mutate(currentState: EarningsState): EarningsState = EarningsState.Error
    }

    data class AddSideEffect(
        val sideEffect: EarningsSideEffect,
    ) : SideEffectTransform<EarningsState, EarningsSideEffect>() {
        override fun mutate(sideEffects: SideEffects<EarningsSideEffect>): SideEffects<EarningsSideEffect> = sideEffects.add(sideEffect)
    }
}
