package com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.transform.StateTransform
import com.rodrirepresa.nursera.core.common.DispatcherProvider
import com.rodrirepresa.nursera.feature.hospital.domain.model.ShiftType
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.GetHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.UpdateHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import com.rodrirepresa.nursera.feature.hospital.presentation.list.ui.ShiftTypeFormData
import com.rodrirepresa.nursera.feature.hospital.presentation.list.ui.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
internal class EditHospitalViewModel
    @Inject
    constructor(
        private val dispatcherProvider: DispatcherProvider,
        private val getHospitalUseCase: GetHospitalUseCase,
        private val updateHospitalUseCase: UpdateHospitalUseCase,
    ) : ViewModel(), MviHost<EditHospitalIntent, State<EditHospitalState, EditHospitalSideEffect>> {
        private val reducer: Reducer<EditHospitalIntent, State<EditHospitalState, EditHospitalSideEffect>> =
            com.adidas.mvi.reducer.Reducer(
                coroutineScope = viewModelScope,
                defaultDispatcher = dispatcherProvider.default(),
                initialInnerState = EditHospitalState.Loading,
                intentExecutor = this::executeIntent,
            )

        override val state: StateFlow<State<EditHospitalState, EditHospitalSideEffect>> = reducer.state

        override fun execute(intent: EditHospitalIntent) {
            reducer.executeIntent(intent)
        }

        private fun executeIntent(
            intent: EditHospitalIntent,
        ): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            when (intent) {
                is EditHospitalIntent.Load -> executeLoad(intent.hospitalId)
                is EditHospitalIntent.UpdateIrpf -> executeUpdateIrpf(intent.value)
                is EditHospitalIntent.AddShift -> executeAddShift()
                is EditHospitalIntent.UpdateShiftAt -> executeUpdateShiftAt(intent.index, intent.shift)
                is EditHospitalIntent.RemoveShift -> executeRemoveShift(intent.index)
                is EditHospitalIntent.Save -> executeSave(intent)
            }

        private fun executeLoad(id: UUID): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            flow {
                val hospital = getHospitalUseCase(id)
                if (hospital == null) {
                    emit(EditHospitalTransform.ShowError)
                    return@flow
                }
                emit(
                    EditHospitalTransform.ShowForm(
                        hospitalId = id,
                        hospitalName = hospital.name,
                        hospitalColor = hospital.color,
                        irpf = hospital.irpf.toFormattedIrpf(),
                        existingShifts = hospital.shifts.map { it.toExistingUiModel() }.toPersistentList(),
                    ),
                )
            }

        private fun executeUpdateIrpf(
            value: String,
        ): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            flow {
                val form = currentForm() ?: return@flow
                emit(rebuildForm(irpf = value, newShifts = form.newShifts))
            }

        private fun executeAddShift(): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            flow {
                val form = currentForm() ?: return@flow
                emit(rebuildForm(irpf = form.irpf, newShifts = form.newShifts + ShiftFormUiState()))
            }

        private fun executeUpdateShiftAt(
            index: Int,
            shift: ShiftFormUiState,
        ): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            flow {
                val form = currentForm() ?: return@flow
                val updated = form.newShifts.toMutableList().also { it[index] = shift }
                emit(rebuildForm(irpf = form.irpf, newShifts = updated))
            }

        private fun executeRemoveShift(
            index: Int,
        ): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            flow {
                val form = currentForm() ?: return@flow
                val updated = form.newShifts.toMutableList().also { it.removeAt(index) }
                emit(rebuildForm(irpf = form.irpf, newShifts = updated))
            }

        private fun executeSave(
            intent: EditHospitalIntent,
        ): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            flow {
                val form = currentForm() ?: return@flow
                emit(EditHospitalTransform.Saving)
                updateHospitalUseCase(
                    id = form.hospitalId,
                    irpf = form.irpf.toFloat(),
                    additionalShifts =
                        form.newShifts
                            .filter { it.isValid() }
                            .map { it.toShiftTypeFormData().toDomain() },
                )
                emit(EditHospitalTransform.AddSideEffect(EditHospitalSideEffect.NavigateBack))
            }

        private fun currentForm(): EditHospitalState.Form? = state.value.view as? EditHospitalState.Form

        private fun rebuildForm(
            irpf: String,
            newShifts: List<ShiftFormUiState>,
        ): EditHospitalTransform.UpdateForm {
            val form = currentForm()
            val irpfError = validateIrpf(irpf)
            val validatedShifts = newShifts.map { it.withValidation() }
            val newShiftsValid = validatedShifts.isEmpty() || validatedShifts.all { it.isValid() }
            val irpfChanged = form != null && irpf != form.originalIrpf
            val hasNewShifts = validatedShifts.isNotEmpty() && validatedShifts.all { it.isValid() }
            val isDirty = irpfChanged || hasNewShifts
            val canSave = isDirty && irpfError == null && irpf.isNotBlank() && newShiftsValid
            return EditHospitalTransform.UpdateForm(
                irpf = irpf,
                irpfError = irpfError,
                newShifts = validatedShifts,
                isDirty = isDirty,
                canSave = canSave,
            )
        }
    }

private fun validateIrpf(value: String): String? {
    if (value.isBlank()) return null
    val f = value.toFloatOrNull() ?: return "Introduce un número válido"
    if (f < 0 || f > 100) return "El IRPF debe estar entre 0 y 100"
    return null
}

private val timeRegex = Regex("""^([01]\d|2[0-3]):[0-5]\d$""")
private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

private fun isValidTime(value: String) = timeRegex.matches(value)

private fun ShiftFormUiState.withValidation(): ShiftFormUiState =
    copy(
        nameError =
            when {
                name.isEmpty() -> null
                name.length > 60 -> "Máximo 60 caracteres"
                else -> null
            },
        startTimeError =
            when {
                startTime.isEmpty() -> null
                !isValidTime(startTime) -> "Formato HH:mm"
                endTime.isNotEmpty() && isValidTime(endTime) &&
                    startTime >= endTime -> "Debe ser anterior al fin"

                else -> null
            },
        endTimeError =
            when {
                endTime.isEmpty() -> null
                !isValidTime(endTime) -> "Formato HH:mm"
                startTime.isNotEmpty() && isValidTime(startTime) &&
                    endTime <= startTime -> "Debe ser posterior al inicio"

                else -> null
            },
        hourlyRateError =
            when {
                hourlyRate.isEmpty() -> null
                hourlyRate.toDoubleOrNull() == null -> "Número inválido"
                hourlyRate.toDouble() <= 0 -> "Debe ser mayor que 0"
                else -> null
            },
    )

private fun ShiftFormUiState.isValid(): Boolean =
    name.isNotBlank() &&
        name.length <= 60 &&
        isValidTime(startTime) &&
        isValidTime(endTime) &&
        startTime < endTime &&
        hourlyRate.toDoubleOrNull()?.let { it > 0 } == true &&
        nameError == null && startTimeError == null && endTimeError == null && hourlyRateError == null

private fun ShiftFormUiState.toShiftTypeFormData(): ShiftTypeFormData =
    ShiftTypeFormData(name = name, startTime = startTime, endTime = endTime, hourlyRate = hourlyRate)

private fun ShiftType.toExistingUiModel(): ExistingShiftUiModel =
    ExistingShiftUiModel(
        name = name,
        schedule = "${startTime.format(timeFormatter)}–${endTime.format(timeFormatter)}",
        hourlyRate = "${"%.2f".format(hourlyRate)}€/h",
    )

private fun Float.toFormattedIrpf(): String =
    if (this == this.toLong().toFloat()) this.toLong().toString() else this.toString()
