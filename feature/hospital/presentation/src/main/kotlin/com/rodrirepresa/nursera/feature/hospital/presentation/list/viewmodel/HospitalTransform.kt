package com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform
import com.rodrirepresa.nursera.feature.hospital.presentation.list.ui.HospitalUiModel

internal object HospitalTransform {
    data class ShowHospitals(
        val hospitals: List<HospitalUiModel>,
    ) : ViewTransform<HospitalState, HospitalSideEffect>() {
        override fun mutate(currentState: HospitalState): HospitalState = HospitalState.Loaded(hospitals)
    }

    data class AddSideEffect(
        val sideEffect: HospitalSideEffect,
    ) : SideEffectTransform<HospitalState, HospitalSideEffect>() {
        override fun mutate(sideEffects: SideEffects<HospitalSideEffect>): SideEffects<HospitalSideEffect> =
            sideEffects.add(sideEffect)
    }
}
