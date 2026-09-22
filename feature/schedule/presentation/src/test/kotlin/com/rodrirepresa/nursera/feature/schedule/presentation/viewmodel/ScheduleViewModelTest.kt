package com.rodrirepresa.nursera.feature.schedule.presentation.viewmodel

import com.rodrirepresa.nursera.core.common.DispatcherProvider
import com.rodrirepresa.nursera.feature.hospital.domain.model.Hospital
import com.rodrirepresa.nursera.feature.hospital.domain.model.ShiftType
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.ObserveHospitalsUseCase
import com.rodrirepresa.nursera.feature.schedule.domain.model.ScheduledShift
import com.rodrirepresa.nursera.feature.schedule.domain.usecase.AddScheduledShiftUseCase
import com.rodrirepresa.nursera.feature.schedule.domain.usecase.DeleteScheduledShiftUseCase
import com.rodrirepresa.nursera.feature.schedule.domain.usecase.ObserveMonthScheduleUseCase
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
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleViewModelTest {
    private val scheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(scheduler)

    private val dispatcherProvider =
        object : DispatcherProvider {
            override fun default() = testDispatcher

            override fun io() = testDispatcher

            override fun main() = testDispatcher
        }

    private val today = LocalDate.now()
    private val currentMonth = YearMonth.from(today)
    private val previousMonth = currentMonth.minusMonths(1)
    private val nextMonth = currentMonth.plusMonths(1)
    private val hospitalId = UUID.randomUUID()
    private val shiftTypeId = UUID.randomUUID()
    private val initialShiftId = UUID.randomUUID()
    private val createdShiftId = UUID.randomUUID()

    private val shiftType =
        ShiftType(
            id = shiftTypeId,
            name = "Morning",
            startTime = LocalTime.of(8, 0),
            endTime = LocalTime.of(15, 0),
            hourlyRate = 20.0,
        )

    private val hospital =
        Hospital(
            id = hospitalId,
            name = "Hospital La Paz",
            color = 0xFFDAF5F0.toInt(),
            irpf = 15f,
            shifts = listOf(shiftType),
        )

    private val hospitalsFlow = MutableStateFlow(listOf(hospital))
    private val monthFlows = mutableMapOf<YearMonth, MutableStateFlow<List<ScheduledShift>>>()
    private val deletedShiftIds = mutableListOf<UUID>()
    private var addShiftCalls = 0

    private val observeHospitalsUseCase =
        object : ObserveHospitalsUseCase {
            override fun invoke(): Flow<List<Hospital>> = hospitalsFlow
        }

    private val observeMonthScheduleUseCase =
        object : ObserveMonthScheduleUseCase {
            override fun invoke(month: YearMonth): Flow<List<ScheduledShift>> = monthFlows.getOrPut(month) { MutableStateFlow(emptyList()) }
        }

    private val addScheduledShiftUseCase =
        object : AddScheduledShiftUseCase {
            override suspend fun invoke(
                date: LocalDate,
                hospitalId: UUID,
                hospitalName: String,
                hospitalColor: Int,
                shiftName: String,
                startTime: LocalTime,
            ): ScheduledShift {
                addShiftCalls += 1
                return ScheduledShift(
                    id = createdShiftId,
                    date = date,
                    hospitalId = hospitalId,
                    hospitalName = hospitalName,
                    hospitalColor = hospitalColor,
                    shiftName = shiftName,
                    startTime = startTime,
                )
            }
        }

    private val deleteScheduledShiftUseCase =
        object : DeleteScheduledShiftUseCase {
            override suspend fun invoke(id: UUID) {
                deletedShiftIds += id
            }
        }

    private lateinit var viewModel: ScheduleViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        monthFlows.clear()
        deletedShiftIds.clear()
        addShiftCalls = 0
        monthFlows[currentMonth] = MutableStateFlow(listOf(makeScheduledShift(id = initialShiftId, date = today)))
        monthFlows[previousMonth] = MutableStateFlow(emptyList())
        monthFlows[nextMonth] = MutableStateFlow(emptyList())
        viewModel =
            ScheduleViewModel(
                dispatcherProvider = dispatcherProvider,
                observeMonthScheduleUseCase = observeMonthScheduleUseCase,
                addScheduledShiftUseCase = addScheduledShiftUseCase,
                deleteScheduledShiftUseCase = deleteScheduledShiftUseCase,
                observeHospitalsUseCase = observeHospitalsUseCase,
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load initializes current month data and active shift status`() =
        runTest(scheduler) {
            advanceUntilIdle()

            val loaded = viewModel.state.value.view as ScheduleState.Loaded
            assertEquals(currentMonth, loaded.currentMonth)
            assertEquals(today, loaded.selectedDate)
            assertNotNull(loaded.monthList[currentMonth])
            assertNotNull(loaded.monthList[previousMonth])
            assertNotNull(loaded.monthList[nextMonth])
            assertTrue(loaded.viewMode is ViewMode.Calendar)
            val todayStatus = loaded.todayStatus as TodayStatusUiModel.ActiveShift
            assertEquals(initialShiftId.toString(), todayStatus.shift.id)
        }

    @Test
    fun `open add shift sheet shows available hospitals`() =
        runTest(scheduler) {
            advanceUntilIdle()

            viewModel.execute(ScheduleIntent.OpenAddShiftSheet)
            advanceUntilIdle()

            val loaded = viewModel.state.value.view as ScheduleState.Loaded
            val sheet = loaded.addShiftSheet as AddShiftSheetUiState.HospitalList
            assertEquals(1, sheet.hospitals.size)
            assertEquals(hospitalId, sheet.hospitals.first().id)
            assertEquals("Hospital La Paz", sheet.hospitals.first().name)
        }

    @Test
    fun `select hospital shows available shifts for that hospital`() =
        runTest(scheduler) {
            advanceUntilIdle()

            viewModel.execute(ScheduleIntent.SelectHospital(hospitalId))
            advanceUntilIdle()

            val loaded = viewModel.state.value.view as ScheduleState.Loaded
            val sheet = loaded.addShiftSheet as AddShiftSheetUiState.ShiftList
            assertEquals(hospitalId, sheet.hospital.id)
            assertEquals(1, sheet.shifts.size)
            assertEquals(shiftTypeId, sheet.shifts.first().id)
            assertEquals("Morning", sheet.shifts.first().name)
        }

    @Test
    fun `add shift appends shift to selected day and closes sheet`() =
        runTest(scheduler) {
            advanceUntilIdle()
            viewModel.execute(ScheduleIntent.SwitchToWeekView)
            advanceUntilIdle()

            viewModel.execute(
                ScheduleIntent.AddShift(
                    hospitalId = hospitalId,
                    hospitalName = hospital.name,
                    hospitalColor = hospital.color,
                    shiftId = shiftTypeId,
                    shiftName = shiftType.name,
                    startTime = shiftType.startTime,
                ),
            )
            advanceUntilIdle()

            val loaded = viewModel.state.value.view as ScheduleState.Loaded
            val weekMode = loaded.viewMode as ViewMode.Week
            assertEquals(2, weekMode.dayShifts.size)
            assertEquals(createdShiftId.toString(), weekMode.dayShifts.last().id)
            assertNull(loaded.addShiftSheet)
            assertEquals(1, addShiftCalls)
            assertEquals(2, loaded.monthList[currentMonth]!!.shiftsByDay[today]!!.size)
        }

    @Test
    fun `delete selected shifts removes them from week state and invokes use case`() =
        runTest(scheduler) {
            advanceUntilIdle()
            viewModel.execute(ScheduleIntent.SwitchToWeekView)
            advanceUntilIdle()
            viewModel.execute(ScheduleIntent.ToggleShiftSelection(initialShiftId.toString()))
            advanceUntilIdle()

            viewModel.execute(ScheduleIntent.DeleteSelectedShifts)
            advanceUntilIdle()

            val loaded = viewModel.state.value.view as ScheduleState.Loaded
            val weekMode = loaded.viewMode as ViewMode.Week
            assertTrue(weekMode.dayShifts.isEmpty())
            assertTrue(deletedShiftIds.contains(initialShiftId))
            assertTrue(loaded.monthList[currentMonth]!!.shiftsByDay[today]!!.isEmpty())
        }

    private fun makeScheduledShift(
        id: UUID,
        date: LocalDate,
    ) = ScheduledShift(
        id = id,
        date = date,
        hospitalId = hospitalId,
        hospitalName = hospital.name,
        hospitalColor = hospital.color,
        shiftName = shiftType.name,
        startTime = shiftType.startTime,
    )
}
