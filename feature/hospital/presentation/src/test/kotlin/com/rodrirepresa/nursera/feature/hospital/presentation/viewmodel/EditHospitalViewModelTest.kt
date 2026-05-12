package com.rodrirepresa.nursera.feature.hospital.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.rodrirepresa.nursera.core.common.DispatcherProvider
import com.rodrirepresa.nursera.feature.hospital.domain.model.Hospital
import com.rodrirepresa.nursera.feature.hospital.domain.model.ShiftType
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.AddShiftToHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.GetHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.EditHospitalIntent
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.EditHospitalSideEffect
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.EditHospitalState
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.EditHospitalViewModel
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
import java.time.LocalTime
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class EditHospitalViewModelTest {
    private val scheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(scheduler)

    private val dispatcherProvider =
        object : DispatcherProvider {
            override fun default() = testDispatcher

            override fun io() = testDispatcher

            override fun main() = testDispatcher
        }

    private val hospitalId = UUID.randomUUID()
    private val hospital =
        Hospital(
            id = hospitalId,
            name = "Hospital La Paz",
            color = 0xFFDAF5F0.toInt(),
            irpf = 15.0f,
            shifts =
                listOf(
                    ShiftType(UUID.randomUUID(), "Mañana", LocalTime.of(8, 0), LocalTime.of(15, 0), 18.50),
                ),
        )

    private var updateCalled = false

    private val getHospital =
        object : GetHospitalUseCase {
            override suspend fun invoke(id: UUID): Hospital? = if (id == hospitalId) hospital else null
        }

    private val updateHospital =
        object : AddShiftToHospitalUseCase {
            override suspend fun invoke(
                id: UUID,
                irpf: Float,
                newShift: List<ShiftType>,
            ) {
                updateCalled = true
            }
        }

    private lateinit var viewModel: EditHospitalViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val savedStateHandle = SavedStateHandle(mapOf("hospitalId" to hospitalId.toString()))
        viewModel = EditHospitalViewModel(dispatcherProvider, getHospital, updateHospital, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() =
        runTest(scheduler) {
            viewModel.state.test {
                assertTrue(awaitItem().view is EditHospitalState.Loading)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Load transitions to Form with hospital data`() =
        runTest(scheduler) {
            viewModel.state.test {
                awaitItem()
                advanceUntilIdle()
                val loaded = awaitItem().view as EditHospitalState.Loaded
                assertEquals("Hospital La Paz", loaded.hospitalName)
                assertEquals("15", loaded.irpf)
                assertEquals("15", loaded.originalIrpf)
                assertEquals(1, loaded.existingShifts.size)
                assertTrue(loaded.newShift.isEmpty())
                assertFalse(loaded.canSave)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Load with unknown id transitions to Error`() =
        runTest(scheduler) {
            val unknownId = UUID.randomUUID()
            val savedStateHandle = SavedStateHandle(mapOf("hospitalId" to unknownId.toString()))
            val vm = EditHospitalViewModel(dispatcherProvider, getHospital, updateHospital, savedStateHandle)
            vm.state.test {
                awaitItem()
                advanceUntilIdle()
                assertTrue(awaitItem().view is EditHospitalState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `UpdateIrpf with same value does not set isDirty`() =
        runTest(scheduler) {
            loadForm()
            viewModel.state.test {
                awaitItem()
                viewModel.execute(EditHospitalIntent.UpdateIrpf("15"))
                advanceUntilIdle()
                val loaded = awaitItem().view as EditHospitalState.Loaded
                assertFalse(loaded.hasChanges)
                assertFalse(loaded.canSave)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `UpdateIrpf with different valid value sets isDirty and canSave`() =
        runTest(scheduler) {
            loadForm()
            viewModel.state.test {
                awaitItem()
                viewModel.execute(EditHospitalIntent.UpdateIrpf("20"))
                advanceUntilIdle()
                val loaded = awaitItem().view as EditHospitalState.Loaded
                assertTrue(loaded.hasChanges)
                assertTrue(loaded.canSave)
                assertNull(loaded.irpfError)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `UpdateIrpf out of range shows error and blocks save`() =
        runTest(scheduler) {
            loadForm()
            viewModel.state.test {
                awaitItem()
                viewModel.execute(EditHospitalIntent.UpdateIrpf("150"))
                advanceUntilIdle()
                val loaded = awaitItem().view as EditHospitalState.Loaded
                assertNotNull(loaded.irpfError)
                assertFalse(loaded.canSave)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `AddShift appends new empty shift`() =
        runTest(scheduler) {
            loadForm()
            viewModel.state.test {
                awaitItem()
                viewModel.execute(EditHospitalIntent.ShowForm)
                advanceUntilIdle()
                val loaded = awaitItem().view as EditHospitalState.Loaded
                assertEquals(1, loaded.newShift.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `valid new shift with unchanged irpf sets isDirty and canSave`() =
        runTest(scheduler) {
            loadForm()
            viewModel.state.test {
                awaitItem()
                viewModel.execute(EditHospitalIntent.ShowForm)
                advanceUntilIdle()
                awaitItem()
                viewModel.execute(
                    EditHospitalIntent.UpdateNewShift(
                        0,
                        ShiftFormUiState(name = "Noche", startTime = "22:00", endTime = "08:00", hourlyRate = "23.0"),
                    ),
                )
                advanceUntilIdle()
                val loaded = awaitItem().view as EditHospitalState.Loaded
                // start >= end so shift is invalid — canSave should still be false
                assertFalse(loaded.canSave)
                assertNotNull(loaded.newShift[0].startTimeError)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `valid new shift with start before end sets canSave true`() =
        runTest(scheduler) {
            loadForm()
            viewModel.state.test {
                awaitItem()
                viewModel.execute(EditHospitalIntent.ShowForm)
                advanceUntilIdle()
                awaitItem()
                viewModel.execute(
                    EditHospitalIntent.UpdateNewShift(
                        0,
                        ShiftFormUiState(name = "Tarde", startTime = "15:00", endTime = "22:00", hourlyRate = "19.0"),
                    ),
                )
                advanceUntilIdle()
                val loaded = awaitItem().view as EditHospitalState.Loaded
                assertTrue(loaded.hasChanges)
                assertTrue(loaded.canSave)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `RemoveShift removes the shift from newShifts`() =
        runTest(scheduler) {
            loadForm()
            viewModel.state.test {
                awaitItem()
                viewModel.execute(EditHospitalIntent.ShowForm)
                advanceUntilIdle()
                awaitItem()
                viewModel.execute(EditHospitalIntent.RemoveShift(0))
                advanceUntilIdle()
                val loaded = awaitItem().view as EditHospitalState.Loaded
                assertTrue(loaded.newShift.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Save triggers update and emits NavigateBack`() =
        runTest(scheduler) {
            loadForm()
            viewModel.state.test {
                awaitItem()
                viewModel.execute(EditHospitalIntent.UpdateIrpf("20"))
                advanceUntilIdle()
                awaitItem()
                viewModel.execute(EditHospitalIntent.SaveShift)
                advanceUntilIdle()
                val saving = awaitItem()
                assertTrue((saving.view as EditHospitalState.Loaded).isSaving)
                val withSideEffect = awaitItem()
                assertTrue(withSideEffect.sideEffects.any { it is EditHospitalSideEffect.NavigateBack })
                assertTrue(updateCalled)
                cancelAndIgnoreRemainingEvents()
            }
        }

    private suspend fun loadForm() {
        advanceUntilIdle()
    }
}
