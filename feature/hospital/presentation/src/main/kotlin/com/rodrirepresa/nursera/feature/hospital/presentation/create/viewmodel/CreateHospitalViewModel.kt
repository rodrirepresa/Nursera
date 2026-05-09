package com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.transform.StateTransform
import com.rodrirepresa.nursera.core.common.DispatcherProvider
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.CreateHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.presentation.list.ui.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

@HiltViewModel
internal class CreateHospitalViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val createHospitalUseCase: CreateHospitalUseCase,
) : ViewModel(), MviHost<CreateHospitalIntent, State<CreateHospitalState, CreateHospitalSideEffect>> {
    private val reducer: Reducer<CreateHospitalIntent, State<CreateHospitalState, CreateHospitalSideEffect>> =
        com.adidas.mvi.reducer.Reducer(
            coroutineScope = viewModelScope,
            defaultDispatcher = dispatcherProvider.default(),
            initialInnerState = CreateHospitalState.Idle,
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
            is CreateHospitalIntent.Save -> executeSave(intent)
        }

    private fun executeSave(
        intent: CreateHospitalIntent.Save,
    ): Flow<StateTransform<State<CreateHospitalState, CreateHospitalSideEffect>>> =
        flow {
            emit(CreateHospitalTransform.Saving)
            createHospitalUseCase(
                name = intent.name,
                color = intent.color,
                irpf = intent.irpf,
                shifts = intent.shifts.map { it.toDomain() },
            )
            emit(CreateHospitalTransform.AddSideEffect(CreateHospitalSideEffect.NavigateBack))
        }
}
