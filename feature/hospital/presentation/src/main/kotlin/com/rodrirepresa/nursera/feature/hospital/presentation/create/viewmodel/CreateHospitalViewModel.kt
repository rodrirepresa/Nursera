package com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.transform.StateTransform
import com.rodrirepresa.nursera.core.common.DispatcherProvider
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.CreateHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.GetRandomHospitalColorUseCase
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import com.rodrirepresa.nursera.feature.hospital.presentation.list.ui.ShiftTypeFormData
import com.rodrirepresa.nursera.feature.hospital.presentation.list.ui.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
internal class CreateHospitalViewModel
    @Inject
    constructor(
        private val dispatcherProvider: DispatcherProvider,
        private val createHospitalUseCase: CreateHospitalUseCase,
        private val getRandomHospitalColorUseCase: GetRandomHospitalColorUseCase,
    ) : ViewModel(), MviHost<CreateHospitalIntent, State<CreateHospitalState, CreateHospitalSideEffect>> {
        private val reducer: Reducer<CreateHospitalIntent, State<CreateHospitalState, CreateHospitalSideEffect>> =
            com.adidas.mvi.reducer.Reducer(
                coroutineScope = viewModelScope,
                defaultDispatcher = dispatcherProvider.default(),
                initialInnerState = CreateHospitalState.Loaded(),
                intentExecutor = this::executeIntent,
            )

        override val state: StateFlow<State<CreateHospitalState, CreateHospitalSideEffect>> = reducer.state

        override fun execute(intent: CreateHospitalIntent) {
            reducer.executeIntent(intent)
        }

        private fun executeIntent(
            intent: CreateHospitalIntent,
        ): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
            when (intent) {
                is CreateHospitalIntent.NavigateBack -> executeAddSideEffect(CreateHospitalSideEffect.NavigateBack)
                is CreateHospitalIntent.UpdateName -> executeUpdateName(intent.value)
                is CreateHospitalIntent.UpdateIrpf -> executeUpdateIrpf(intent.value)
                is CreateHospitalIntent.AddShift -> executeAddShift()
                is CreateHospitalIntent.UpdateShiftAt -> executeUpdateShiftAt(intent.index, intent.shift)
                is CreateHospitalIntent.RemoveShift -> executeRemoveShift(intent.index)
                is CreateHospitalIntent.Save -> executeSave()
            }

        private fun executeUpdateName(
            value: String,
        ): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
            flow {
                val form = currentForm() ?: return@flow
                emit(rebuildForm(name = value.take(30), irpf = form.irpf, shifts = form.shifts))
            }

        private fun executeUpdateIrpf(
            value: String,
        ): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
            flow {
                val form = currentForm() ?: return@flow
                emit(rebuildForm(name = form.name, irpf = value, shifts = form.shifts))
            }

        private fun executeAddShift(): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
            flow {
                val form = currentForm() ?: return@flow
                emit(rebuildForm(name = form.name, irpf = form.irpf, shifts = form.shifts + ShiftFormUiState()))
            }

        private fun executeUpdateShiftAt(
            index: Int,
            shift: ShiftFormUiState,
        ): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
            flow {
                val form = currentForm() ?: return@flow
                val newShifts = form.shifts.toMutableList().also { it[index] = shift }
                emit(rebuildForm(name = form.name, irpf = form.irpf, shifts = newShifts))
            }

        private fun executeRemoveShift(
            index: Int,
        ): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
            flow {
                val form = currentForm() ?: return@flow
                if (form.shifts.size <= 1) return@flow
                val newShifts = form.shifts.toMutableList().also { it.removeAt(index) }
                emit(rebuildForm(name = form.name, irpf = form.irpf, shifts = newShifts))
            }

        private fun executeSave(): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
            flow {
                val form = currentForm() ?: return@flow
                emit(CreateHospitalTransform.Saving)
                val color = getRandomHospitalColorUseCase()
                createHospitalUseCase(
                    name = form.name,
                    color = color,
                    irpf = form.irpf.toFloat(),
                    shifts = form.shifts.map { it.toShiftTypeFormData().toDomain() },
                )
                emit(CreateHospitalTransform.AddSideEffect(CreateHospitalSideEffect.NavigateBack))
            }

        private fun executeAddSideEffect(
            sideEffect: CreateHospitalSideEffect,
        ): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
            flow { emit(CreateHospitalTransform.AddSideEffect(sideEffect)) }

        private fun currentForm(): CreateHospitalState.Loaded? = state.value.view as? CreateHospitalState.Loaded

        private fun rebuildForm(
            name: String,
            irpf: String,
            shifts: List<ShiftFormUiState>,
        ): CreateHospitalTransform.UpdateForm {
            val nameError = validateName(name)
            val irpfError = validateIrpf(irpf)
            val validatedShifts = shifts.map { it.withValidation() }
            val canSave =
                name.isNotBlank() && nameError == null &&
                    irpf.isNotBlank() && irpfError == null &&
                    validatedShifts.isNotEmpty() && validatedShifts.all { it.isValid() }
            return CreateHospitalTransform.UpdateForm(
                name = name,
                nameError = nameError,
                irpf = irpf,
                irpfError = irpfError,
                shifts = validatedShifts.toPersistentList(),
                canSave = canSave,
            )
        }
    }

private fun validateName(name: String): String? =
    when {
        name.isBlank() -> null
        name.length > 30 -> "Máximo 30 caracteres"
        else -> null
    }

private fun validateIrpf(value: String): String? {
    if (value.isBlank()) return null
    val f = value.toFloatOrNull() ?: return "Introduce un número válido"
    if (f < 0 || f > 100) return "El IRPF debe estar entre 0 y 100"
    return null
}

private val timeRegex = Regex("""^([01]\d|2[0-3]):[0-5]\d$""")

private fun isValidTime(value: String) = timeRegex.matches(value)

private fun ShiftFormUiState.withValidation(): ShiftFormUiState =
    copy(
        nameError =
            when {
                name.isEmpty() -> null
                name.length > 30 -> "Máximo 30 caracteres"
                else -> null
            },
        startTimeError =
            when {
                startTime.isEmpty() -> null
                !isValidTime(startTime) -> "Formato HH:mm"
                endTime.isNotEmpty() && isValidTime(endTime) && startTime >= endTime -> "Debe ser anterior al fin"
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
        name.length <= 30 &&
        isValidTime(startTime) &&
        isValidTime(endTime) &&
        startTime < endTime &&
        hourlyRate.toDoubleOrNull()?.let { it > 0 } == true &&
        nameError == null && startTimeError == null && endTimeError == null && hourlyRateError == null

private fun ShiftFormUiState.toShiftTypeFormData(): ShiftTypeFormData =
    ShiftTypeFormData(name = name, startTime = startTime, endTime = endTime, hourlyRate = hourlyRate)
