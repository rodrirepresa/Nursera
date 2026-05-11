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
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.AddShiftToHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.DeleteShiftsFromHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.GetHospitalUseCase
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
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
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
        private val getHospitalUseCase: GetHospitalUseCase,
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
                is EditHospitalIntent.AddShift -> executeAddShift()
                is EditHospitalIntent.UpdateNewShift -> executeUpdateShift(intent.shift)
                is EditHospitalIntent.ToggleExistingShiftSelection -> executeToggleExistingShiftSelection(intent.index)
                is EditHospitalIntent.DeleteSelectedShifts -> executeDeleteSelectedShifts()
                is EditHospitalIntent.SaveShift -> executeSaveShift()
            }

        @OptIn(ExperimentalCoroutinesApi::class)
        private fun executeLoad(id: UUID): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            observeHospitalByIdUseCase(id).transformLatest {
                val hospital = getHospitalUseCase(id)
                if (hospital == null) {
                    emit(EditHospitalTransform.ShowError)
                    return@transformLatest
                }
                emit(
                    EditHospitalTransform.ShowForm(
                        hospitalId = id,
                        hospitalName = hospital.name,
                        hospitalColor = hospital.color,
                        irpf = hospital.irpf.toFormattedIrpf(),
                        existingShifts = hospital.shifts.map { it.toShiftUiModel() }.toPersistentList(),
                    ),
                )
            }

        private fun executeUpdateIrpf(
            value: String,
        ): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            flow {
                val form = (state.value.view as? EditHospitalState.Loaded) ?: return@flow
                emit(
                    EditHospitalTransform.UpdateIrpf(
                        irpf = value,
                    ),
                )
                if (validateIrpf(value) == null && value.isNotBlank()) {
                    updateHospitalIrpfUseCase(id = form.hospitalId, irpf = value.toFloat())
                }
            }

        private fun executeAddShift(): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            flow {
                val form = (state.value.view as? EditHospitalState.Loaded) ?: return@flow
                if (form.newShift != null) return@flow
                emit(
                    rebuildForm(
                        newShift = ShiftFormUiState(),
                    ),
                )
            }

        private fun executeUpdateShift(
            shift: ShiftFormUiState,
        ): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            flow {
                emit(rebuildForm(newShift = shift))
            }

        private fun executeToggleExistingShiftSelection(
            index: Int,
        ): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            flow { emit(EditHospitalTransform.ToggleExistingShiftSelection(index)) }

        private fun executeDeleteSelectedShifts(): Flow<
            StateTransform<
                State<EditHospitalState, EditHospitalSideEffect>,
                >,
            > =
            flow {
                val state = state.value.view as? EditHospitalState.Loaded ?: error("Wrong state")
                val remaining =
                    state.existingShifts.filterIndexed { i, _ -> i !in state.selectedShiftIndices }
                        .toPersistentList()
                emit(EditHospitalTransform.DeleteSelectedShifts(remaining))
                deleteShiftsFromHospitalUseCase(
                    hospitalId = state.hospitalId,
                    shiftsIds = state.selectedShiftIndices.map { state.existingShifts[it].id },
                )
            }

        private fun executeSaveShift(): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            flow {
                val form = state.value.view as? EditHospitalState.Loaded ?: return@flow
                val newShift = form.newShift
                if (newShift != null && newShift.isValid()) {
                    addShiftToHospitalUseCase(
                        hospitalId = form.hospitalId,
                        name = newShift.name,
                        startTime = newShift.startTime.toLocalTime(),
                        endTime = newShift.endTime.toLocalTime(),
                        hourlyRate = newShift.hourlyRate.toDouble(),
                    )
                }
                emit(EditHospitalTransform.Saving(false))
            }.onStart {
                emit(EditHospitalTransform.Saving(true))
            }

        private fun executeAddSideEffect(
            sideEffect: EditHospitalSideEffect,
        ): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
            flow { emit(EditHospitalTransform.AddSideEffect(sideEffect)) }

        private fun rebuildForm(newShift: ShiftFormUiState?): EditHospitalTransform.UpdateForm {
            val canSave = newShift != null && newShift.withValidation().isValid()
            return EditHospitalTransform.UpdateForm(
                newShift = newShift?.withValidation(),
                canSave = canSave,
            )
        }
    }
