package com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.validators.validateIrpf
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet

internal object EditHospitalTransform {
    data class ShowForm(
        val hospitalId: java.util.UUID,
        val hospitalName: String,
        val hospitalColor: Int,
        val irpf: String,
        val existingShifts: ImmutableList<ShiftUiModel>,
    ) : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState =
            EditHospitalState.Loaded(
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

    data class UpdateIrpf(
        val irpf: String,
    ) : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState {
            if (currentState !is EditHospitalState.Loaded) return currentState
            return currentState.copy(
                irpf = irpf,
                irpfError = validateIrpf(irpf),
            )
        }
    }

    data class UpdateForm(
        val newShift: ShiftFormUiState?,
        val canSave: Boolean,
    ) : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState {
            if (currentState !is EditHospitalState.Loaded) return currentState
            return currentState.copy(
                newShift = newShift,
                canSave = canSave,
                selectedShiftIndices = persistentSetOf(),
            )
        }
    }

    data class ToggleExistingShiftSelection(
        val index: Int,
    ) : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState {
            if (currentState !is EditHospitalState.Loaded) return currentState
            val updated =
                currentState.selectedShiftIndices.toMutableSet().apply {
                    if (contains(index)) remove(index) else add(index)
                }
            return currentState.copy(selectedShiftIndices = updated.toPersistentSet())
        }
    }

    data class DeleteSelectedShifts(
        private val shift: List<ShiftUiModel>,
    ) : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState {
            if (currentState !is EditHospitalState.Loaded) return currentState

            return currentState.copy(
                existingShifts = shift.toPersistentList(),
                selectedShiftIndices = kotlinx.collections.immutable.persistentSetOf(),
            )
        }
    }

    data class Saving(
        val isSaving: Boolean,
    ) : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState {
            if (currentState !is EditHospitalState.Loaded) return currentState
            return currentState.copy(isSaving = isSaving)
        }
    }

    data class AddSideEffect(
        val sideEffect: EditHospitalSideEffect,
    ) : SideEffectTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(sideEffects: SideEffects<EditHospitalSideEffect>): SideEffects<EditHospitalSideEffect> =
            sideEffects.add(sideEffect)
    }
}
