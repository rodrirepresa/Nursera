package com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import kotlinx.collections.immutable.toPersistentList

internal object CreateHospitalTransform {
    data class ShowLoaded(
        val hospitalColor: Int,
    ) : ViewTransform<CreateHospitalState, CreateHospitalSideEffect>() {
        override fun mutate(currentState: CreateHospitalState): CreateHospitalState = CreateHospitalState.Loaded(hospitalColor = hospitalColor)
    }

    object ShowError : ViewTransform<CreateHospitalState, CreateHospitalSideEffect>() {
        override fun mutate(currentState: CreateHospitalState): CreateHospitalState = CreateHospitalState.Error
    }

    data class UpdateForm(
        val name: String,
        val nameError: String?,
        val irpf: String,
        val irpfError: String?,
        val canSave: Boolean,
    ) : ViewTransform<CreateHospitalState, CreateHospitalSideEffect>() {
        override fun mutate(currentState: CreateHospitalState): CreateHospitalState {
            if (currentState !is CreateHospitalState.Loaded) return currentState
            return currentState.copy(
                name = name,
                nameError = nameError,
                irpf = irpf,
                irpfError = irpfError,
                canSave = canSave,
            )
        }
    }

    object OpenShiftSheet : ViewTransform<CreateHospitalState, CreateHospitalSideEffect>() {
        override fun mutate(currentState: CreateHospitalState): CreateHospitalState {
            if (currentState !is CreateHospitalState.Loaded) return currentState
            return currentState.copy(shiftForm = ShiftFormUiState(), canSaveShiftForm = false)
        }
    }

    object DismissShiftSheet : ViewTransform<CreateHospitalState, CreateHospitalSideEffect>() {
        override fun mutate(currentState: CreateHospitalState): CreateHospitalState {
            if (currentState !is CreateHospitalState.Loaded) return currentState
            return currentState.copy(shiftForm = null, isShiftFormSaving = false, canSaveShiftForm = false)
        }
    }

    data class UpdateFormShift(
        val form: ShiftFormUiState,
        val canSave: Boolean,
    ) : ViewTransform<CreateHospitalState, CreateHospitalSideEffect>() {
        override fun mutate(currentState: CreateHospitalState): CreateHospitalState {
            if (currentState !is CreateHospitalState.Loaded) return currentState
            return currentState.copy(shiftForm = form, canSaveShiftForm = canSave)
        }
    }

    data class SetShiftFormSaving(val isSaving: Boolean) :
        ViewTransform<CreateHospitalState, CreateHospitalSideEffect>() {
        override fun mutate(currentState: CreateHospitalState): CreateHospitalState {
            if (currentState !is CreateHospitalState.Loaded) return currentState
            return currentState.copy(isShiftFormSaving = isSaving)
        }
    }

    data class ConfirmShift(
        val confirmed: CreateShiftItem,
    ) : ViewTransform<CreateHospitalState, CreateHospitalSideEffect>() {
        override fun mutate(currentState: CreateHospitalState): CreateHospitalState {
            if (currentState !is CreateHospitalState.Loaded) return currentState
            val newConfirmed = (currentState.shifts + confirmed).toPersistentList()
            return currentState.copy(
                shifts = newConfirmed,
                shiftForm = null,
                isShiftFormSaving = false,
                canSaveShiftForm = false,
                canSave = computeCanSave(currentState.name, currentState.irpf, newConfirmed),
            )
        }
    }

    data class ToggleShiftSelection(val id: String) : ViewTransform<CreateHospitalState, CreateHospitalSideEffect>() {
        override fun mutate(currentState: CreateHospitalState): CreateHospitalState {
            if (currentState !is CreateHospitalState.Loaded) return currentState
            return currentState.copy(
                shifts =
                    currentState.shifts.map { item ->
                        if (item.id == id) item.copy(isSelected = !item.isSelected) else item
                    }.toPersistentList(),
            )
        }
    }

    object DeleteSelectedShifts : ViewTransform<CreateHospitalState, CreateHospitalSideEffect>() {
        override fun mutate(currentState: CreateHospitalState): CreateHospitalState {
            if (currentState !is CreateHospitalState.Loaded) return currentState
            val remaining = currentState.shifts.filterNot { it.isSelected }.toPersistentList()
            return currentState.copy(
                shifts = remaining,
                canSave = computeCanSave(currentState.name, currentState.irpf, remaining),
            )
        }
    }

    object Saving : ViewTransform<CreateHospitalState, CreateHospitalSideEffect>() {
        override fun mutate(currentState: CreateHospitalState): CreateHospitalState {
            if (currentState !is CreateHospitalState.Loaded) return currentState
            return currentState.copy(isSaving = true)
        }
    }

    data class AddSideEffect(
        val sideEffect: CreateHospitalSideEffect,
    ) : SideEffectTransform<CreateHospitalState, CreateHospitalSideEffect>() {
        override fun mutate(sideEffects: SideEffects<CreateHospitalSideEffect>): SideEffects<CreateHospitalSideEffect> = sideEffects.add(sideEffect)
    }
}

internal fun computeCanSave(
    name: String,
    irpf: String,
    confirmedShifts: List<CreateShiftItem>,
): Boolean =
    name.isNotBlank() && name.length <= 30 &&
        irpf.isNotBlank() && irpf.toFloatOrNull()?.let { it in 0f..100f } == true &&
        confirmedShifts.isNotEmpty()
