package com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.transform.StateTransform
import com.rodrirepresa.nursera.core.common.DispatcherProvider
import com.rodrirepresa.nursera.feature.hospital.domain.model.Hospital
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.AddShiftToHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.DeleteShiftsFromHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.ObserveHospitalByIdUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.UpdateHospitalIrpfUseCase
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.mappers.toFormattedIrpf
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.mappers.toLocalTime
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.mappers.toShiftUiModel
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.validators.isValid
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.validators.validateIrpf
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.validators.withValidation
import com.rodrirepresa.nursera.feature.hospital.presentation.list.navigation.EditHospital
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformLatest
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
internal class EditHospitalViewModel
    @Inject
    constructor(
        dispatcherProvider: DispatcherProvider,
        private val addShiftToHospitalUseCase: AddShiftToHospitalUseCase,
        private val updateHospitalIrpfUseCase: UpdateHospitalIrpfUseCase,
        private val observeHospitalByIdUseCase: ObserveHospitalByIdUseCase,
        private val deleteShiftsFromHospitalUseCase: DeleteShiftsFromHospitalUseCase,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel(), MviHost<EditHospitalIntent, State<EditHospitalState, EditHospitalSideEffect>> {
        private val hospitalId: UUID = UUID.fromString(savedStateHandle.toRoute<EditHospital>().hospitalId)

        private val reducer: Reducer<EditHospitalIntent, State<EditHospitalState, EditHospitalSideEffect>> =
            com.adidas.mvi.reducer.Reducer(
                coroutineScope = viewModelScope,
                defaultDispatcher = dispatcherProvider.default(),
                initialInnerState = EditHospitalState.Loading,
                intentExecutor = this::executeIntent,
            )

        override val state: StateFlow<State<EditHospitalState, EditHospitalSideEffect>> = reducer.state

        init {
            execute(EditHospitalIntent.Load)
        }

        override fun execute(intent: EditHospitalIntent) {
            reducer.executeIntent(intent)
        }

        private fun executeIntent(
            intent: EditHospitalIntent,
        ): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            when (intent) {
                is EditHospitalIntent.Load -> executeLoad(hospitalId)
                is EditHospitalIntent.NavigateBack -> executeAddSideEffect(EditHospitalSideEffect.NavigateBack)
                is EditHospitalIntent.UpdateIrpf -> executeUpdateIrpf(intent.value)
                is EditHospitalIntent.ShowForm -> executeShowForm()
                is EditHospitalIntent.UpdateNewShift -> executeUpdateShift(intent.shift)
                is EditHospitalIntent.ToggleExistingShiftSelection ->
                    executeToggleExistingShiftSelection(
                        intent.shiftId,
                    )

                is EditHospitalIntent.DeleteSelectedShifts -> executeDeleteSelectedShifts()
                is EditHospitalIntent.SaveShift -> executeSaveShift()
            }

        @OptIn(ExperimentalCoroutinesApi::class)
        private fun executeLoad(id: UUID): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            observeHospitalByIdUseCase(
                id,
            ).transformLatest<Hospital, StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> { hospital ->
                emit(
                    EditHospitalTransform.ShowForm(
                        hospitalId = id,
                        hospitalName = hospital.name,
                        hospitalColor = hospital.color,
                        irpf = hospital.irpf.toFormattedIrpf(),
                        existingShifts =
                            hospital.shifts.sortedBy { it.startTime }
                                .map { it.toShiftUiModel() },
                    ),
                )
            }.catch {
                emit(EditHospitalTransform.ShowError)
            }

        private fun executeUpdateIrpf(
            value: String,
        ): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            flow {
                val loadedState = (state.value.view as? EditHospitalState.Loaded) ?: return@flow
                emit(EditHospitalTransform.UpdateIrpf(irpf = value))
                if (validateIrpf(value) == null && value.isNotBlank()) {
                    updateHospitalIrpfUseCase(id = loadedState.hospitalId, irpf = value.toFloat())
                }
            }

        private fun executeShowForm(): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            flow {
                val loaded = state.value.view as? EditHospitalState.Loaded ?: return@flow
                val form = loaded.shifts.filterIsInstance<ShiftItem.Form>().firstOrNull() ?: return@flow
                if (!form.isVisible) emit(EditHospitalTransform.SetFormVisible(true))
            }

        private fun executeUpdateShift(
            shift: ShiftFormUiState,
        ): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            flow {
                val validated = shift.withValidation()
                emit(EditHospitalTransform.UpdateFormShift(form = validated, canSave = validated.isValid()))
            }

        private fun executeToggleExistingShiftSelection(
            shiftId: UUID,
        ): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            flow { emit(EditHospitalTransform.ToggleExistingShiftSelection(shiftId)) }

        private fun executeDeleteSelectedShifts(): Flow<
            StateTransform<
                State<EditHospitalState, EditHospitalSideEffect>,
                >,
            > =
            flow {
                val loadedState = state.value.view as? EditHospitalState.Loaded ?: error("Wrong state")
                val selectedIds =
                    loadedState.shifts
                        .filterIsInstance<ShiftItem.Existing>()
                        .filter { it.isSelected }
                        .map { it.model.id }
                deleteShiftsFromHospitalUseCase(
                    hospitalId = loadedState.hospitalId,
                    shiftsIds = selectedIds,
                )
                emit(EditHospitalTransform.DeleteSelectedShifts)
            }

        private fun executeSaveShift(): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            flow {
                val loadedState = state.value.view as? EditHospitalState.Loaded ?: return@flow
                val formItem = loadedState.shifts.filterIsInstance<ShiftItem.Form>().firstOrNull() ?: return@flow
                if (formItem.form.isValid()) {
                    addShiftToHospitalUseCase(
                        hospitalId = loadedState.hospitalId,
                        name = formItem.form.name,
                        startTime = formItem.form.startTime.toLocalTime(),
                        endTime = formItem.form.endTime.toLocalTime(),
                        hourlyRate = formItem.form.hourlyRate.toDouble(),
                    )
                    emit(EditHospitalTransform.SetFormVisible(false))
                }
                emit(EditHospitalTransform.SetFormSaving(false))
            }.onStart {
                emit(EditHospitalTransform.SetFormSaving(true))
            }

        private fun executeAddSideEffect(
            sideEffect: EditHospitalSideEffect,
        ): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            flow { emit(EditHospitalTransform.AddSideEffect(sideEffect)) }
    }
