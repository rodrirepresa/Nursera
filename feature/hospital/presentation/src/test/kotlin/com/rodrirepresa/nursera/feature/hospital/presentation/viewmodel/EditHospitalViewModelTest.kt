package com.rodrirepresa.nursera.feature.hospital.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.rodrirepresa.nursera.core.common.DispatcherProvider
import com.rodrirepresa.nursera.feature.hospital.domain.model.Hospital
import com.rodrirepresa.nursera.feature.hospital.domain.model.ShiftType
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.AddShiftToHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.DeleteShiftsFromHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.ObserveHospitalByIdUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.UpdateHospitalIrpfUseCase
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.EditHospitalIntent
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.EditHospitalState
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.EditHospitalViewModel
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.ShiftItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
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
    private val shiftId = UUID.randomUUID()
    private val hospitalFlow =
        MutableStateFlow(
            Hospital(
                id = hospitalId,
                name = "Hospital La Paz",
                color = 0xFFDAF5F0.toInt(),
                irpf = 15.0f,
                shifts =
                    listOf(
                        ShiftType(shiftId, "Mañana", LocalTime.of(8, 0), LocalTime.of(15, 0), 18.50),
                    ),
            ),
        )

    private val observeHospitalByIdUseCase =
        object : ObserveHospitalByIdUseCase {
            override fun invoke(id: UUID): Flow<Hospital> = hospitalFlow
        }

    private val updateHospitalIrpfUseCase =
        object : UpdateHospitalIrpfUseCase {
            override suspend fun invoke(
                id: UUID,
                irpf: Float,
            ) {}
        }

    private var addShiftCalled = false
    private val addShiftToHospitalUseCase =
        object : AddShiftToHospitalUseCase {
            override suspend fun invoke(
                hospitalId: UUID,
                name: String,
                startTime: LocalTime,
                endTime: LocalTime,
                hourlyRate: Double,
            ) {
                addShiftCalled = true
            }
        }

    private val deleteShiftsFromHospitalUseCase =
        object : DeleteShiftsFromHospitalUseCase {
            override suspend fun invoke(
                hospitalId: UUID,
                shiftsIds: List<UUID>,
            ) {}
        }

    private lateinit var viewModel: EditHospitalViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel =
            EditHospitalViewModel(
                dispatcherProvider = dispatcherProvider,
                addShiftToHospitalUseCase = addShiftToHospitalUseCase,
                updateHospitalIrpfUseCase = updateHospitalIrpfUseCase,
                observeHospitalByIdUseCase = observeHospitalByIdUseCase,
                deleteShiftsFromHospitalUseCase = deleteShiftsFromHospitalUseCase,
                savedStateHandle = SavedStateHandle(mapOf("hospitalId" to hospitalId.toString())),
            )
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
    fun `Load transitions to Loaded with hospital data`() =
        runTest(scheduler) {
            viewModel.state.test {
                awaitItem()
                advanceUntilIdle()
                val loaded = awaitItem().view as EditHospitalState.Loaded
                assertEquals("Hospital La Paz", loaded.hospitalName)
                assertEquals("15", loaded.irpf)
                assertEquals(1, loaded.shifts.size)
                assertTrue(loaded.shifts[0] is ShiftItem.Existing)
                assertNull(loaded.shiftForm)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `OpenShiftSheet opens bottom sheet with empty form`() =
        runTest(scheduler) {
            advanceUntilIdle()
            viewModel.state.test {
                awaitItem()
                viewModel.execute(EditHospitalIntent.OpenShiftSheet)
                advanceUntilIdle()
                val loaded = awaitItem().view as EditHospitalState.Loaded
                assertNotNull(loaded.shiftForm)
                assertEquals("", loaded.shiftForm!!.name)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `DismissShiftSheet clears the form`() =
        runTest(scheduler) {
            advanceUntilIdle()
            viewModel.execute(EditHospitalIntent.OpenShiftSheet)
            advanceUntilIdle()
            viewModel.state.test {
                awaitItem()
                viewModel.execute(EditHospitalIntent.DismissShiftSheet)
                advanceUntilIdle()
                val loaded = awaitItem().view as EditHospitalState.Loaded
                assertNull(loaded.shiftForm)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `UpdateNewShift with valid form enables canSaveShiftForm`() =
        runTest(scheduler) {
            advanceUntilIdle()
            viewModel.execute(EditHospitalIntent.OpenShiftSheet)
            advanceUntilIdle()
            viewModel.state.test {
                awaitItem()
                viewModel.execute(
                    EditHospitalIntent.UpdateNewShift(
                        ShiftFormUiState(name = "Tarde", startTime = "15:00", endTime = "22:00", hourlyRate = "19.0"),
                    ),
                )
                advanceUntilIdle()
                val loaded = awaitItem().view as EditHospitalState.Loaded
                assertTrue(loaded.canSaveShiftForm)
                assertNull(loaded.shiftForm!!.nameError)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `UpdateIrpf out of range shows irpfError`() =
        runTest(scheduler) {
            advanceUntilIdle()
            viewModel.state.test {
                awaitItem()
                viewModel.execute(EditHospitalIntent.UpdateIrpf("150"))
                advanceUntilIdle()
                val loaded = awaitItem().view as EditHospitalState.Loaded
                assertNotNull(loaded.irpfError)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `SaveShift with valid form calls use case and dismisses sheet`() =
        runTest(scheduler) {
            advanceUntilIdle()
            viewModel.execute(EditHospitalIntent.OpenShiftSheet)
            advanceUntilIdle()
            viewModel.execute(
                EditHospitalIntent.UpdateNewShift(
                    ShiftFormUiState(name = "Tarde", startTime = "15:00", endTime = "22:00", hourlyRate = "19.0"),
                ),
            )
            advanceUntilIdle()
            viewModel.state.test {
                awaitItem()
                viewModel.execute(EditHospitalIntent.SaveShift)
                advanceUntilIdle()
                // saving = true emitted first
                val saving = awaitItem().view as EditHospitalState.Loaded
                assertTrue(saving.isShiftFormSaving)
                // then dismissed
                val dismissed = awaitItem().view as EditHospitalState.Loaded
                assertNull(dismissed.shiftForm)
                assertTrue(addShiftCalled)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `ToggleExistingShiftSelection toggles isSelected on shift`() =
        runTest(scheduler) {
            advanceUntilIdle()
            viewModel.state.test {
                awaitItem()
                viewModel.execute(EditHospitalIntent.ToggleExistingShiftSelection(shiftId))
                advanceUntilIdle()
                val loaded = awaitItem().view as EditHospitalState.Loaded
                val existing = loaded.shifts.filterIsInstance<ShiftItem.Existing>().first { it.model.id == shiftId }
                assertTrue(existing.isSelected)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
