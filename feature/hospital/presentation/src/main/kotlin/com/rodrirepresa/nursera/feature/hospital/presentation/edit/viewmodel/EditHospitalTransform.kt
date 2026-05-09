package com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

internal object EditHospitalTransform {
    data class ShowForm(
        val hospitalId: java.util.UUID,
        val hospitalName: String,
        val hospitalColor: Int,
        val irpf: String,
        val existingShifts: ImmutableList<ExistingShiftUiModel>,
    ) : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState =
            EditHospitalState.Form(
                hospitalId = hospitalId,
                hospitalName = hospitalName,
                hospitalColor = hospitalColor,
                originalIrpf = irpf,
                irpf = irpf,
                existingShifts = existingShifts,
            )
    }

    object ShowError : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState = EditHospitalState.Error
    }

    data class UpdateForm(
        val irpf: String,
        val irpfError: String?,
        val newShifts: List<ShiftFormUiState>,
        val isDirty: Boolean,
        val canSave: Boolean,
    ) : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState {
            if (currentState !is EditHospitalState.Form) return currentState
            return currentState.copy(
                irpf = irpf,
                irpfError = irpfError,
                newShifts = newShifts.toPersistentList(),
                isDirty = isDirty,
                canSave = canSave,
            )
        }
    }

    object Saving : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState = EditHospitalState.Saving
    }

    data class AddSideEffect(
        val sideEffect: EditHospitalSideEffect,
    ) : SideEffectTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(sideEffects: SideEffects<EditHospitalSideEffect>): SideEffects<EditHospitalSideEffect> =
            sideEffects.add(sideEffect)
    }
}
