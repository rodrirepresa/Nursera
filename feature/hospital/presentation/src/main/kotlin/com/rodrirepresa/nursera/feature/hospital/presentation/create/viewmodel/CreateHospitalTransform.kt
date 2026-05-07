package com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform

internal object CreateHospitalTransform {
    object Saving : ViewTransform<CreateHospitalState, CreateHospitalSideEffect>() {
        override fun mutate(currentState: CreateHospitalState): CreateHospitalState = CreateHospitalState.Saving
    }

    data class AddSideEffect(
        val sideEffect: CreateHospitalSideEffect,
    ) : SideEffectTransform<CreateHospitalState, CreateHospitalSideEffect>() {
        override fun mutate(sideEffects: SideEffects<CreateHospitalSideEffect>): SideEffects<CreateHospitalSideEffect> =
            sideEffects.add(sideEffect)
    }
}
