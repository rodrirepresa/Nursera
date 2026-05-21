package com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.validators.validateIrpf
import kotlinx.collections.immutable.toPersistentList
import java.util.UUID

internal object EditHospitalTransform {
    data class ShowLoaded(
        val hospitalId: UUID,
        val hospitalName: String,
        val hospitalColor: Int,
        val irpf: String,
        val existingShifts: List<ShiftUiModel>,
    ) : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState {
            val previous = currentState as? EditHospitalState.Loaded
            val validIds = existingShifts.map { it.id }.toSet()
            val newShifts =
                existingShifts.map { model ->
                    val wasSelected =
                        previous?.shifts
                            ?.filterIsInstance<ShiftItem.Existing>()
                            ?.find { it.model.id == model.id }
                            ?.isSelected ?: false
                    ShiftItem.Existing(model, isSelected = wasSelected && model.id in validIds)
                }
            return EditHospitalState.Loaded(
                hospitalId = hospitalId,
                hospitalName = hospitalName,
                hospitalColor = hospitalColor,
                originalIrpf = previous?.originalIrpf ?: irpf,
                irpf = previous?.irpf ?: irpf,
                irpfError = previous?.irpfError,
                shifts = newShifts.toPersistentList(),
                shiftForm = previous?.shiftForm,
                isShiftFormSaving = previous?.isShiftFormSaving ?: false,
                canSaveShiftForm = previous?.canSaveShiftForm ?: false,
            )
        }
    }

    object ShowError : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState = EditHospitalState.Error
    }

    data class UpdateIrpf(
        val irpf: String,
    ) : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState {
            if (currentState !is EditHospitalState.Loaded) return currentState
            return currentState.copy(irpf = irpf, irpfError = validateIrpf(irpf))
        }
    }

    object OpenShiftSheet : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState {
            if (currentState !is EditHospitalState.Loaded) return currentState
            return currentState.copy(shiftForm = ShiftFormUiState(), canSaveShiftForm = false)
        }
    }

    object DismissShiftSheet : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState {
            if (currentState !is EditHospitalState.Loaded) return currentState
            return currentState.copy(shiftForm = null, isShiftFormSaving = false, canSaveShiftForm = false)
        }
    }

    data class UpdateFormShift(
        val form: ShiftFormUiState,
        val canSave: Boolean,
    ) : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState {
            if (currentState !is EditHospitalState.Loaded) return currentState
            return currentState.copy(shiftForm = form, canSaveShiftForm = canSave)
        }
    }

    data class SetShiftFormSaving(val isSaving: Boolean) : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState {
            if (currentState !is EditHospitalState.Loaded) return currentState
            return currentState.copy(isShiftFormSaving = isSaving)
        }
    }

    data class ToggleExistingShiftSelection(
        val shiftId: UUID,
    ) : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState {
            if (currentState !is EditHospitalState.Loaded) return currentState
            return currentState.copy(
                shifts =
                    currentState.shifts.map { item ->
                        if (item is ShiftItem.Existing && item.model.id == shiftId) {
                            item.copy(isSelected = !item.isSelected)
                        } else {
                            item
                        }
                    }.toPersistentList(),
            )
        }
    }

    object DeleteSelectedShifts : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState {
            if (currentState !is EditHospitalState.Loaded) return currentState
            return currentState.copy(
                shifts = currentState.shifts.filterNot { it is ShiftItem.Existing && it.isSelected }.toPersistentList(),
            )
        }
    }

    data class AddSideEffect(
        val sideEffect: EditHospitalSideEffect,
    ) : SideEffectTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(sideEffects: SideEffects<EditHospitalSideEffect>): SideEffects<EditHospitalSideEffect> = sideEffects.add(sideEffect)
    }
}
