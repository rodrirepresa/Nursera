package com.rodrirepresa.nursera.feature.hospital.presentation.viewmodel

import app.cash.turbine.test
import com.adidas.mvi.State
import com.rodrirepresa.nursera.core.common.DispatcherProvider
import com.rodrirepresa.nursera.feature.hospital.domain.model.ShiftType
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.CreateHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalIntent
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalSideEffect
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalState
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalViewModel
import com.rodrirepresa.nursera.feature.hospital.presentation.list.ui.ShiftTypeFormData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateHospitalViewModelTest {
    private val scheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(scheduler)

    private val dispatcherProvider =
        object : DispatcherProvider {
            override fun default() = testDispatcher

            override fun io() = testDispatcher

            override fun main() = testDispatcher
        }

    private var createCalled = false

    private val createHospital =
        object : CreateHospitalUseCase {
            override suspend fun invoke(
                name: String,
                color: Int,
                irpf: Float,
                shifts: List<ShiftType>,
            ) {
                createCalled = true
            }
        }

    private lateinit var viewModel: CreateHospitalViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CreateHospitalViewModel(dispatcherProvider, createHospital)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() =
        runTest(scheduler) {
            viewModel.state.test {
                assertTrue(awaitItem().view is CreateHospitalState.Idle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `save transitions to Saving then emits NavigateBack side effect`() =
        runTest(scheduler) {
            viewModel.state.test {
                assertTrue(awaitItem().view is CreateHospitalState.Idle)

                viewModel.execute(
                    CreateHospitalIntent.Save(
                        name = "Test Hospital",
                        color = 0xFF1565C0.toInt(),
                        irpf = 15.0f,
                        shifts = listOf(ShiftTypeFormData("Mañana", "08:00", "15:00", "18.5")),
                    ),
                )
                advanceUntilIdle()

                val saving = awaitItem()
                assertTrue(saving.view is CreateHospitalState.Saving)

                val withSideEffect: State<CreateHospitalState, CreateHospitalSideEffect> = awaitItem()
                assertTrue(withSideEffect.sideEffects.any { it is CreateHospitalSideEffect.NavigateBack })
                assertTrue(createCalled)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
