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
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.validators.isValid
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.validators.validateIrpf
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.validators.withValidation
import com.rodrirepresa.nursera.feature.hospital.presentation.list.ui.ShiftTypeFormData
import com.rodrirepresa.nursera.feature.hospital.presentation.list.ui.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import java.util.UUID
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
                initialInnerState = CreateHospitalState.Loading,
                intentExecutor = this::executeIntent,
            )

        override val state: StateFlow<State<CreateHospitalState, CreateHospitalSideEffect>> = reducer.state

        init {
            execute(CreateHospitalIntent.Load)
        }

        override fun execute(intent: CreateHospitalIntent) {
            reducer.executeIntent(intent)
        }

        private fun executeIntent(intent: CreateHospitalIntent): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
            when (intent) {
                is CreateHospitalIntent.Load -> executeLoad()
                is CreateHospitalIntent.NavigateBack -> executeAddSideEffect(CreateHospitalSideEffect.NavigateBack)
                is CreateHospitalIntent.UpdateName -> executeUpdateName(intent.value)
                is CreateHospitalIntent.UpdateIrpf -> executeUpdateIrpf(intent.value)
                is CreateHospitalIntent.OpenShiftSheet -> executeOpenShiftSheet()
                is CreateHospitalIntent.DismissShiftSheet -> executeDismissShiftSheet()
                is CreateHospitalIntent.UpdateNewShift -> executeUpdateNewShift(intent.shift)
                is CreateHospitalIntent.SaveShift -> executeSaveShift()
                is CreateHospitalIntent.ToggleShiftSelection -> executeToggleShiftSelection(intent.id)
                is CreateHospitalIntent.DeleteSelectedShifts -> executeDeleteSelectedShifts()
                is CreateHospitalIntent.Save -> executeSave()
            }

        private fun executeLoad(): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
            flow {
                try {
                    val color = getRandomHospitalColorUseCase()
                    emit(CreateHospitalTransform.ShowLoaded(hospitalColor = color))
                } catch (e: Exception) {
                    emit(CreateHospitalTransform.ShowError)
                }
            }

        private fun executeUpdateName(value: String): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
            flow {
                val loaded = currentLoaded() ?: return@flow
                val nameError = validateName(value)
                emit(
                    CreateHospitalTransform.UpdateForm(
                        name = value,
                        nameError = nameError,
                        irpf = loaded.irpf,
                        irpfError = loaded.irpfError,
                        canSave = nameError == null && computeCanSave(value, loaded.irpf, loaded.shifts),
                    ),
                )
            }

        private fun executeUpdateIrpf(value: String): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
            flow {
                val loaded = currentLoaded() ?: return@flow
                val irpfError = validateIrpf(value)
                emit(
                    CreateHospitalTransform.UpdateForm(
                        name = loaded.name,
                        nameError = loaded.nameError,
                        irpf = value,
                        irpfError = irpfError,
                        canSave = irpfError == null && computeCanSave(loaded.name, value, loaded.shifts),
                    ),
                )
            }

        private fun executeOpenShiftSheet(): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
            flow { emit(CreateHospitalTransform.OpenShiftSheet) }

        private fun executeDismissShiftSheet(): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
            flow { emit(CreateHospitalTransform.DismissShiftSheet) }

        private fun executeUpdateNewShift(shift: ShiftFormUiState): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
            flow {
                val validated = shift.withValidation()
                emit(CreateHospitalTransform.UpdateFormShift(form = validated, canSave = validated.isValid()))
            }

        private fun executeSaveShift(): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
            flow {
                val loaded = currentLoaded() ?: return@flow
                if (!loaded.canSaveShiftForm) {
                    emit(CreateHospitalTransform.SetShiftFormSaving(false))
                    return@flow
                }
                val form = loaded.shiftForm ?: return@flow
                val confirmed =
                    CreateShiftItem(
                        form = form,
                        id = UUID.randomUUID().toString(),
                    )
                emit(CreateHospitalTransform.ConfirmShift(confirmed))
            }.onStart {
                emit(CreateHospitalTransform.SetShiftFormSaving(true))
            }

        private fun executeToggleShiftSelection(id: String): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
            flow { emit(CreateHospitalTransform.ToggleShiftSelection(id)) }

        private fun executeDeleteSelectedShifts(): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
            flow { emit(CreateHospitalTransform.DeleteSelectedShifts) }

        private fun executeSave(): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
            flow {
                val loaded = currentLoaded() ?: return@flow
                emit(CreateHospitalTransform.Saving)
                val color = getRandomHospitalColorUseCase()
                createHospitalUseCase(
                    name = loaded.name,
                    color = color,
                    irpf = loaded.irpf.toFloat(),
                    shifts = loaded.shifts.map { it.form.toShiftTypeFormData().toDomain() },
                )
                emit(CreateHospitalTransform.AddSideEffect(CreateHospitalSideEffect.NavigateBack))
            }

        private fun executeAddSideEffect(
            sideEffect: CreateHospitalSideEffect,
        ): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
            flow { emit(CreateHospitalTransform.AddSideEffect(sideEffect)) }

        private fun currentLoaded(): CreateHospitalState.Loaded? = state.value.view as? CreateHospitalState.Loaded
    }

private fun validateName(name: String): String? =
    when {
        name.length > 30 -> "Máximo 30 caracteres"
        else -> null
    }

private fun ShiftFormUiState.toShiftTypeFormData(): ShiftTypeFormData =
    ShiftTypeFormData(name = name, startTime = startTime, endTime = endTime, hourlyRate = hourlyRate)
