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
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel.EditHospitalSideEffect
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalTime
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
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
                shifts = listOf(ShiftType(shiftId, "Morning", LocalTime.of(8, 0), LocalTime.of(15, 0), 18.50)),
            ),
        )

    private val observeHospitalByIdUseCase =
        object : ObserveHospitalByIdUseCase {
            override fun invoke(id: UUID): Flow<Hospital> = hospitalFlow
        }

    private var updatedIrpf: Float? = null
    private val updateHospitalIrpfUseCase =
        object : UpdateHospitalIrpfUseCase {
            override suspend fun invoke(
                id: UUID,
                irpf: Float,
            ) {
                updatedIrpf = irpf
            }
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

    private var deletedIds: List<UUID> = emptyList()
    private val deleteShiftsFromHospitalUseCase =
        object : DeleteShiftsFromHospitalUseCase {
            override suspend fun invoke(
                hospitalId: UUID,
                shiftsIds: List<UUID>,
            ) {
                deletedIds = shiftsIds
            }
        }

    private lateinit var viewModel: EditHospitalViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        updatedIrpf = null
        addShiftCalled = false
        deletedIds = emptyList()
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
    fun `load populates hospital data`() =
        runTest(scheduler) {
            viewModel.state.test {
                val loading = awaitItem().view
                assertTrue(loading is EditHospitalState.Loading)

                val loaded = awaitItem().view as EditHospitalState.Loaded
                assertEquals("Hospital La Paz", loaded.hospitalName)
                assertEquals("15", loaded.irpf)
                assertEquals(1, loaded.shifts.size)
                assertTrue(loaded.shifts.first() is ShiftItem.Existing)
                assertNull(loaded.shiftForm)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `open and dismiss shift sheet toggles form`() =
        runTest(scheduler) {
            viewModel.state.test {
                awaitItem()
                awaitItem()

                viewModel.execute(EditHospitalIntent.OpenShiftSheet)
                advanceUntilIdle()
                val opened = awaitItem().view as EditHospitalState.Loaded
                assertNotNull(opened.shiftForm)

                viewModel.execute(EditHospitalIntent.DismissShiftSheet)
                advanceUntilIdle()
                val dismissed = awaitItem().view as EditHospitalState.Loaded
                assertNull(dismissed.shiftForm)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `update valid irpf persists change`() =
        runTest(scheduler) {
            viewModel.state.test {
                awaitItem()
                awaitItem()

                viewModel.execute(EditHospitalIntent.UpdateIrpf("20"))
                advanceUntilIdle()

                val state = awaitItem().view as EditHospitalState.Loaded
                assertEquals("20", state.irpf)
                assertEquals(20f, updatedIrpf)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `update irpf out of range shows validation error`() =
        runTest(scheduler) {
            viewModel.state.test {
                awaitItem()
                awaitItem()

                viewModel.execute(EditHospitalIntent.UpdateIrpf("150"))
                advanceUntilIdle()

                val state = awaitItem().view as EditHospitalState.Loaded
                assertNotNull(state.irpfError)
                assertNull(updatedIrpf)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `save valid shift calls use case and dismisses sheet`() =
        runTest(scheduler) {
            viewModel.state.test {
                awaitItem()
                awaitItem()

                viewModel.execute(EditHospitalIntent.OpenShiftSheet)
                advanceUntilIdle()
                awaitItem()

                viewModel.execute(
                    EditHospitalIntent.UpdateNewShift(
                        ShiftFormUiState(name = "Evening", startTime = "15:00", endTime = "22:00", hourlyRate = "19.0"),
                    ),
                )
                advanceUntilIdle()

                viewModel.execute(EditHospitalIntent.SaveShift)
                advanceUntilIdle()

                val state = viewModel.state.value.view as EditHospitalState.Loaded
                assertTrue(addShiftCalled)
                assertNull(state.shiftForm)
                assertFalse(state.isShiftFormSaving)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `toggle existing shift selection marks item selected`() =
        runTest(scheduler) {
            viewModel.state.test {
                awaitItem()
                awaitItem()

                viewModel.execute(EditHospitalIntent.ToggleExistingShiftSelection(shiftId))
                advanceUntilIdle()

                val state = awaitItem().view as EditHospitalState.Loaded
                val existing = state.shifts.filterIsInstance<ShiftItem.Existing>().first { it.model.id == shiftId }
                assertTrue(existing.isSelected)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `delete selected shifts removes item and calls use case`() =
        runTest(scheduler) {
            viewModel.state.test {
                awaitItem()
                awaitItem()

                viewModel.execute(EditHospitalIntent.ToggleExistingShiftSelection(shiftId))
                advanceUntilIdle()
                awaitItem()

                viewModel.execute(EditHospitalIntent.DeleteSelectedShifts)
                advanceUntilIdle()

                val state = awaitItem().view as EditHospitalState.Loaded
                assertEquals(listOf(shiftId), deletedIds)
                assertTrue(state.shifts.none { it is ShiftItem.Existing && it.model.id == shiftId })
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `navigate back emits side effect`() =
        runTest(scheduler) {
            viewModel.state.test {
                awaitItem()
                awaitItem()

                viewModel.execute(EditHospitalIntent.NavigateBack)
                advanceUntilIdle()

                val state = awaitItem()
                assertTrue(state.sideEffects.any { it is EditHospitalSideEffect.NavigateBack })
                cancelAndIgnoreRemainingEvents()
            }
        }
}
