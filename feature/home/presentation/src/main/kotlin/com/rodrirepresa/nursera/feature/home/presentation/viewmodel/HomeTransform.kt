package com.rodrirepresa.nursera.feature.home.presentation.viewmodel

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform

internal object HomeTransform {
    object Loaded : ViewTransform<HomeState, HomeSideEffect>() {
        override fun mutate(currentState: HomeState): HomeState {
            return HomeState.Loaded
        }
    }

    data class AddSideEffect(
        val sideEffect: HomeSideEffect,
    ) : SideEffectTransform<HomeState, HomeSideEffect>() {
        override fun mutate(sideEffects: SideEffects<HomeSideEffect>): SideEffects<HomeSideEffect> {
            return sideEffects.add(sideEffect)
        }
    }
}
