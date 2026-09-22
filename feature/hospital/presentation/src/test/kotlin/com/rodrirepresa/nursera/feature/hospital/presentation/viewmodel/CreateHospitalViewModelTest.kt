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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class CreateHospitalViewModelTest {
    private val scheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(scheduler)

    private val dispatcherProvider = object : DispatcherProvider {
        override fun default() = testDispatcher
        override fun io() = testDispatcher
        override fun main() = testDispatcher
    }

    private var createCalled = false
    private var lastCreatedColor = 0
    private var throwOnRandomColor = false

    private val createHospital = object : CreateHospitalUseCase {
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
    private val getRandomColor = object : GetRandomHospitalColorUseCase {
        override fun invoke(): Int {
            if (throwOnRandomColor) error("color unavailable")
            return fixedColor
        }
    }

    private lateinit var viewModel: CreateHospitalViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        throwOnRandomColor = false
        createCalled = false
        lastCreatedColor = 0
        viewModel = CreateHospitalViewModel(dispatcherProvider, createHospital, getRandomColor)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load initializes with random color and empty shifts`() = runTest(scheduler) {
        viewModel.state.test {
            val loading = awaitItem().view
            assertTrue(loading is CreateHospitalState.Loading)

            val loaded = awaitItem().view as CreateHospitalState.Loaded
            assertEquals(fixedColor, loaded.hospitalColor)
            assertEquals(0, loaded.shifts.size)
            assertFalse(loaded.canSave)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `update name validates length and keeps clean state`() = runTest(scheduler) {
        viewModel.state.test {
            awaitItem()
            awaitItem()

            viewModel.execute(CreateHospitalIntent.UpdateName("A".repeat(31)))
            advanceUntilIdle()

            val state = awaitItem().view as CreateHospitalState.Loaded
            assertEquals("A".repeat(31), state.name)
            assertNotNull(state.nameError)
            assertFalse(state.canSave)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `navigate back emits side effect`() = runTest(scheduler) {
        viewModel.state.test {
            awaitItem()
            awaitItem()

            viewModel.execute(CreateHospitalIntent.NavigateBack)
            advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state.sideEffects.any { it is CreateHospitalSideEffect.NavigateBack })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `open and dismiss shift sheet updates form state`() = runTest(scheduler) {
        viewModel.state.test {
            awaitItem()
            awaitItem()

            viewModel.execute(CreateHospitalIntent.OpenShiftSheet)
            advanceUntilIdle()
            val opened = awaitItem().view as CreateHospitalState.Loaded
            assertNotNull(opened.shiftForm)

            viewModel.execute(CreateHospitalIntent.DismissShiftSheet)
            advanceUntilIdle()
            val dismissed = awaitItem().view as CreateHospitalState.Loaded
            assertNull(dismissed.shiftForm)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `update irpf detects invalid values`() = runTest(scheduler) {
        viewModel.state.test {
            awaitItem()
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
    fun `valid shift form can be saved`() = runTest(scheduler) {
        viewModel.state.test {
            awaitItem()
            awaitItem()

            viewModel.execute(CreateHospitalIntent.OpenShiftSheet)
            advanceUntilIdle()
            awaitItem()

            viewModel.execute(
                CreateHospitalIntent.UpdateNewShift(
                    ShiftFormUiState(name = "Morning", startTime = "08:00", endTime = "15:00", hourlyRate = "22.5"),
                ),
            )
            advanceUntilIdle()

            val state = awaitItem().view as CreateHospitalState.Loaded
            assertTrue(state.canSaveShiftForm)
            assertNull(state.shiftForm?.startTimeError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `save valid shift adds shift to hospital`() = runTest(scheduler) {
        viewModel.state.test {
            awaitItem()
            awaitItem()

            viewModel.execute(CreateHospitalIntent.OpenShiftSheet)
            advanceUntilIdle()
            awaitItem()

            viewModel.execute(
                CreateHospitalIntent.UpdateNewShift(
                    ShiftFormUiState(name = "Morning", startTime = "08:00", endTime = "15:00", hourlyRate = "22.5"),
                ),
            )
            advanceUntilIdle()

            viewModel.execute(CreateHospitalIntent.SaveShift)
            advanceUntilIdle()

            val state = viewModel.state.value.view as CreateHospitalState.Loaded
            assertEquals(1, state.shifts.size)
            assertNull(state.shiftForm)
            assertFalse(state.isShiftFormSaving)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `save hospital creates record and navigates back`() = runTest(scheduler) {
        viewModel.state.test {
            awaitItem()
            awaitItem()

            viewModel.execute(CreateHospitalIntent.UpdateName("Hospital La Paz"))
            advanceUntilIdle()
            awaitItem()

            viewModel.execute(CreateHospitalIntent.UpdateIrpf("15"))
            advanceUntilIdle()
            awaitItem()

            viewModel.execute(CreateHospitalIntent.OpenShiftSheet)
            advanceUntilIdle()
            awaitItem()

            viewModel.execute(
                CreateHospitalIntent.UpdateNewShift(
                    ShiftFormUiState(name = "Morning", startTime = "08:00", endTime = "15:00", hourlyRate = "18.5"),
                ),
            )
            advanceUntilIdle()

            viewModel.execute(CreateHospitalIntent.SaveShift)
            advanceUntilIdle()

            viewModel.execute(CreateHospitalIntent.Save)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertTrue((state.view as CreateHospitalState.Loaded).isSaving)
            assertTrue(state.sideEffects.any { it is CreateHospitalSideEffect.NavigateBack })
            assertTrue(createCalled)
            assertEquals(fixedColor, lastCreatedColor)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load fails gracefully when random color lookup errors`() = runTest(scheduler) {
        throwOnRandomColor = true
        viewModel = CreateHospitalViewModel(dispatcherProvider, createHospital, getRandomColor)

        viewModel.state.test {
            awaitItem()
            val error = awaitItem().view
            assertTrue(error is CreateHospitalState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
