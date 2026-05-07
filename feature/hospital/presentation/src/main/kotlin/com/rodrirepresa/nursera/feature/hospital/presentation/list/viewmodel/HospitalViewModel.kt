package com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.transform.StateTransform
import com.rodrirepresa.nursera.core.common.DispatcherProvider
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.DeleteHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.ObserveHospitalsUseCase
import com.rodrirepresa.nursera.feature.hospital.presentation.list.ui.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
internal class HospitalViewModel
    @Inject
    constructor(
        private val dispatcherProvider: DispatcherProvider,
        private val observeHospitalsUseCase: ObserveHospitalsUseCase,
        private val deleteHospitalUseCase: DeleteHospitalUseCase,
    ) : ViewModel(), MviHost<HospitalIntent, State<HospitalState, HospitalSideEffect>> {
        private val reducer: Reducer<HospitalIntent, State<HospitalState, HospitalSideEffect>> =
            com.adidas.mvi.reducer.Reducer(
                coroutineScope = viewModelScope,
                defaultDispatcher = dispatcherProvider.default(),
                initialInnerState = HospitalState.Loading,
                intentExecutor = this::executeIntent,
            )

        override val state: StateFlow<State<HospitalState, HospitalSideEffect>> = reducer.state

        override fun execute(intent: HospitalIntent) {
            reducer.executeIntent(intent)
        }

        private fun executeIntent(
            intent: HospitalIntent,
        ): Flow<StateTransform<State<HospitalState, HospitalSideEffect>>> =
            when (intent) {
                is HospitalIntent.Load -> executeLoad()
                is HospitalIntent.DeleteHospital -> executeDeleteHospital(intent)
            }

        private fun executeLoad(): Flow<StateTransform<State<HospitalState, HospitalSideEffect>>> =
            observeHospitalsUseCase().map { hospitals ->
                HospitalTransform.ShowHospitals(hospitals.map { it.toUiModel() })
            }

        private fun executeDeleteHospital(
            intent: HospitalIntent.DeleteHospital,
        ): Flow<StateTransform<State<HospitalState, HospitalSideEffect>>> =
            flow {
                deleteHospitalUseCase(intent.id)
            }
    }
