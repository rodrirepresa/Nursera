package com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

internal object CreateHospitalTransform {
    data class UpdateForm(
        val name: String,
        val nameError: String?,
        val irpf: String,
        val irpfError: String?,
        val shifts: ImmutableList<ShiftFormUiState>,
        val canSave: Boolean,
    ) : ViewTransform<CreateHospitalState, CreateHospitalSideEffect>() {
        override fun mutate(currentState: CreateHospitalState): CreateHospitalState =
            CreateHospitalState.Loaded(
                name = name,
                nameError = nameError,
                irpf = irpf,
                irpfError = irpfError,
                shifts = shifts,
                canSave = canSave,
            )
    }

    data class AddShift(
        val shifts: List<ShiftFormUiState>,
    ) : ViewTransform<CreateHospitalState, CreateHospitalSideEffect>() {
        override fun mutate(currentState: CreateHospitalState): CreateHospitalState {
            if (currentState !is CreateHospitalState.Loaded) return currentState
            return currentState.copy(shifts = shifts.toPersistentList())
        }
    }

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
