package com.rodrirepresa.nursera.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.transform.StateTransform
import com.rodrirepresa.nursera.core.common.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel
    @Inject
    constructor(
        private val dispatcherProvider: DispatcherProvider,
    ) : ViewModel(), MviHost<HomeIntent, State<HomeState, HomeSideEffect>> {
        private val reducer: Reducer<HomeIntent, State<HomeState, HomeSideEffect>> =
            com.adidas.mvi.reducer.Reducer(
                coroutineScope = viewModelScope,
                defaultDispatcher = dispatcherProvider.default(),
                initialInnerState = HomeState.Loading,
                intentExecutor = this::executeIntent,
            )

        override val state: StateFlow<State<HomeState, HomeSideEffect>> = reducer.state

        override fun execute(intent: HomeIntent) {
            reducer.executeIntent(intent)
        }

        private fun executeIntent(intent: HomeIntent): Flow<StateTransform<State<HomeState, HomeSideEffect>>> =
            when (intent) {
                is HomeIntent.Load -> executeLoad()
            }

        private fun executeLoad() =
            flow {
                emit(HomeTransform.Loaded)
            }
    }
