package com.rodrirepresa.nursera.feature.profile.presentation.viewmodel

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform
import java.time.YearMonth

internal object ProfileTransform {
    data class InitLoaded(
        val month: YearMonth,
    ) : ViewTransform<ProfileState, ProfileSideEffect>() {
        override fun mutate(currentState: ProfileState): ProfileState = ProfileState.Loaded(currentMonth = month)
    }

    data class SetDisplayedMonth(
        val month: YearMonth,
    ) : ViewTransform<ProfileState, ProfileSideEffect>() {
        override fun mutate(currentState: ProfileState): ProfileState {
            if (currentState !is ProfileState.Loaded) return currentState
            return currentState.copy(currentMonth = month)
        }
    }

    data class UpsertMonthSummary(
        val month: YearMonth,
        val summary: ProfileMonthSummaryUiModel,
    ) : ViewTransform<ProfileState, ProfileSideEffect>() {
        override fun mutate(currentState: ProfileState): ProfileState {
            if (currentState !is ProfileState.Loaded) return currentState
            return currentState.copy(summariesByMonth = currentState.summariesByMonth.put(month, summary))
        }
    }

    object ShowError : ViewTransform<ProfileState, ProfileSideEffect>() {
        override fun mutate(currentState: ProfileState): ProfileState = ProfileState.Error
    }

    data class AddSideEffect(
        val sideEffect: ProfileSideEffect,
    ) : SideEffectTransform<ProfileState, ProfileSideEffect>() {
        override fun mutate(sideEffects: SideEffects<ProfileSideEffect>): SideEffects<ProfileSideEffect> = sideEffects.add(sideEffect)
    }
}
