package com.rodrirepresa.nursera.feature.hospital.presentation.viewmodel

import app.cash.turbine.test
import com.adidas.mvi.State
import com.rodrirepresa.nursera.core.common.DispatcherProvider
import com.rodrirepresa.nursera.feature.hospital.domain.model.ShiftType
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.CreateHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.GetRandomHospitalColorUseCase
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalIntent
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalSideEffect
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalState
import com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel.CreateHospitalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
    private var lastCreatedColor: Int = 0

    private val createHospital =
        object : CreateHospitalUseCase {
            override suspend fun invoke(
                name: String,
                color: Int,
                irpf: Float,
                shifts: List<ShiftType>,
            ) {
                createCalled = true
                lastCreatedColor = color
            }
        }

    private val fixedColor = 0xFFDAF5F0.toInt()
    private val getRandomColor =
        object : GetRandomHospitalColorUseCase {
            override fun invoke(): Int = fixedColor
        }

    private lateinit var viewModel: CreateHospitalViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CreateHospitalViewModel(dispatcherProvider, createHospital, getRandomColor)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Form with one empty shift`() =
        runTest(scheduler) {
            viewModel.state.test {
                val state = awaitItem().view
                assertTrue(state is CreateHospitalState.Loaded)
                val loaded = state as CreateHospitalState.Loaded
                assertEquals(1, loaded.shifts.size)
                assertFalse(loaded.canSave)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `UpdateName trims to 60 chars and clears error for valid name`() =
        runTest(scheduler) {
            viewModel.state.test {
                awaitItem()
                viewModel.execute(CreateHospitalIntent.UpdateName("Hospital La Paz"))
                advanceUntilIdle()
                val state = awaitItem().view as CreateHospitalState.Loaded
                assertEquals("Hospital La Paz", state.name)
                assertNull(state.nameError)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `UpdateName caps at 60 characters`() =
        runTest(scheduler) {
            viewModel.state.test {
                awaitItem()
                val longName = "A".repeat(70)
                viewModel.execute(CreateHospitalIntent.UpdateName(longName))
                advanceUntilIdle()
                val state = awaitItem().view as CreateHospitalState.Loaded
                assertEquals(60, state.name.length)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `UpdateIrpf shows error for out-of-range value`() =
        runTest(scheduler) {
            viewModel.state.test {
                awaitItem()
                viewModel.execute(CreateHospitalIntent.UpdateIrpf("150"))
                advanceUntilIdle()
                val state = awaitItem().view as CreateHospitalState.Loaded
                assertNotNull(state.irpfError)
                assertFalse(state.canSave)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `AddShift appends a new empty shift`() =
        runTest(scheduler) {
            viewModel.state.test {
                awaitItem()
                viewModel.execute(CreateHospitalIntent.AddShift)
                advanceUntilIdle()
                val state = awaitItem().view as CreateHospitalState.Loaded
                assertEquals(2, state.shifts.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `RemoveShift does nothing when only one shift remains`() =
        runTest(scheduler) {
            viewModel.state.test {
                awaitItem()
                viewModel.execute(CreateHospitalIntent.RemoveShift(0))
                advanceUntilIdle()
                expectNoEvents()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `canSave is true when all fields are valid`() =
        runTest(scheduler) {
            viewModel.state.test {
                awaitItem()

                viewModel.execute(CreateHospitalIntent.UpdateName("Hospital La Paz"))
                advanceUntilIdle()
                awaitItem()

                viewModel.execute(CreateHospitalIntent.UpdateIrpf("15"))
                advanceUntilIdle()
                awaitItem()

                viewModel.execute(
                    CreateHospitalIntent.UpdateShiftAt(
                        0,
                        ShiftFormUiState(name = "Mañana", startTime = "08:00", endTime = "15:00", hourlyRate = "18.5"),
                    ),
                )
                advanceUntilIdle()
                val state = awaitItem().view as CreateHospitalState.Loaded
                assertTrue(state.canSave)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `shift start hour after end hour blocks canSave`() =
        runTest(scheduler) {
            viewModel.state.test {
                awaitItem()

                viewModel.execute(CreateHospitalIntent.UpdateName("Hospital"))
                advanceUntilIdle()
                awaitItem()

                viewModel.execute(CreateHospitalIntent.UpdateIrpf("15"))
                advanceUntilIdle()
                awaitItem()

                viewModel.execute(
                    CreateHospitalIntent.UpdateShiftAt(
                        0,
                        ShiftFormUiState(name = "Turno", startTime = "15:00", endTime = "08:00", hourlyRate = "18.5"),
                    ),
                )
                advanceUntilIdle()
                val state = awaitItem().view as CreateHospitalState.Loaded
                assertFalse(state.canSave)
                assertNotNull(state.shifts[0].startTimeError)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Save transitions to Saving then emits NavigateBack and uses random color`() =
        runTest(scheduler) {
            viewModel.state.test {
                awaitItem()

                viewModel.execute(CreateHospitalIntent.UpdateName("Hospital La Paz"))
                advanceUntilIdle()
                awaitItem()

                viewModel.execute(CreateHospitalIntent.UpdateIrpf("15"))
                advanceUntilIdle()
                awaitItem()

                viewModel.execute(
                    CreateHospitalIntent.UpdateShiftAt(
                        0,
                        ShiftFormUiState(name = "Mañana", startTime = "08:00", endTime = "15:00", hourlyRate = "18.5"),
                    ),
                )
                advanceUntilIdle()
                awaitItem()

                viewModel.execute(CreateHospitalIntent.Save)
                advanceUntilIdle()

                val saving = awaitItem()
                assertTrue(saving.view is CreateHospitalState.Saving)

                val withSideEffect: State<CreateHospitalState, CreateHospitalSideEffect> = awaitItem()
                assertTrue(withSideEffect.sideEffects.any { it is CreateHospitalSideEffect.NavigateBack })
                assertTrue(createCalled)
                assertEquals(fixedColor, lastCreatedColor)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
