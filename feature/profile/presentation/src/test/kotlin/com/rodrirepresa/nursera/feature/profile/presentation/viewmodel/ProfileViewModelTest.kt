package com.rodrirepresa.nursera.feature.profile.presentation.viewmodel

import app.cash.turbine.test
import com.rodrirepresa.nursera.core.common.DispatcherProvider
import com.rodrirepresa.nursera.feature.hospital.domain.model.Hospital
import com.rodrirepresa.nursera.feature.hospital.domain.model.ShiftType
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.ObserveHospitalsUseCase
import com.rodrirepresa.nursera.feature.schedule.domain.model.ScheduledShift
import com.rodrirepresa.nursera.feature.schedule.domain.usecase.ObserveMonthScheduleUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalTime
import java.time.YearMonth
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    private val scheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(scheduler)

    private val dispatcherProvider =
        object : DispatcherProvider {
            override fun default() = testDispatcher

            override fun io() = testDispatcher

            override fun main() = testDispatcher
        }

    private val initialMonth = YearMonth.now()
    private val nextMonth = initialMonth.plusMonths(1)
    private val hospitalId = UUID.randomUUID()
    private val shiftType =
        ShiftType(
            id = UUID.randomUUID(),
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

    private val observeHospitalsUseCase =
        object : ObserveHospitalsUseCase {
            override fun invoke(): Flow<List<Hospital>> = hospitalsFlow
        }

    private val observeMonthScheduleUseCase =
        object : ObserveMonthScheduleUseCase {
            override fun invoke(month: YearMonth): Flow<List<ScheduledShift>> = monthFlows.getOrPut(month) { MutableStateFlow(emptyList()) }
        }

    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        monthFlows[initialMonth] =
            MutableStateFlow(
                listOf(
                    ScheduledShift(
                        id = UUID.randomUUID(),
                        date = initialMonth.atDay(10),
                        hospitalId = hospitalId,
                        hospitalName = hospital.name,
                        hospitalColor = hospital.color,
                        shiftName = shiftType.name,
                        startTime = shiftType.startTime,
                    ),
                ),
            )
        monthFlows[nextMonth] = MutableStateFlow(emptyList())
        viewModel =
            ProfileViewModel(
                dispatcherProvider = dispatcherProvider,
                observeMonthScheduleUseCase = observeMonthScheduleUseCase,
                observeHospitalsUseCase = observeHospitalsUseCase,
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `profile summary updates when shifts for displayed month change`() =
        runTest(scheduler) {
            viewModel.state.test {
                skipItems(1)
                advanceUntilIdle()
                val loaded = awaitItem().view as ProfileState.Loaded
                val month = loaded.currentMonth
                val summary = loaded.summariesByMonth[month]
                if (summary == null) {
                    val next = awaitItem().view as ProfileState.Loaded
                    val nextSummary = next.summariesByMonth[month]!!
                    assertEquals(140.0, nextSummary.totalGrossAmount, 0.001)
                    assertEquals(119.0, nextSummary.totalNetAmount, 0.001)
                } else {
                    assertEquals(140.0, summary.totalGrossAmount, 0.001)
                    assertEquals(119.0, summary.totalNetAmount, 0.001)
                }

                val monthFlow = monthFlows[month]!!
                monthFlow.value =
                    monthFlow.value +
                    ScheduledShift(
                        id = UUID.randomUUID(),
                        date = month.atDay(15),
                        hospitalId = hospitalId,
                        hospitalName = hospital.name,
                        hospitalColor = hospital.color,
                        shiftName = shiftType.name,
                        startTime = shiftType.startTime,
                    )

                advanceUntilIdle()
                val updated = awaitItem().view as ProfileState.Loaded
                val updatedSummary = updated.summariesByMonth[month]!!
                assertEquals(280.0, updatedSummary.totalGrossAmount, 0.001)
                assertEquals(238.0, updatedSummary.totalNetAmount, 0.001)
                assertEquals(2, updatedSummary.totalShifts)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `changing month updates selected month and loads its summary`() =
        runTest(scheduler) {
            advanceUntilIdle()
            viewModel.execute(ProfileIntent.SetDisplayedMonth(nextMonth))
            advanceUntilIdle()

            val loaded = viewModel.state.value.view as ProfileState.Loaded
            assertEquals(nextMonth, loaded.currentMonth)
            val summary = loaded.summariesByMonth[nextMonth]
            assertTrue(summary != null)
            assertEquals(0.0, summary!!.totalGrossAmount, 0.001)
            assertEquals(0.0, summary.totalNetAmount, 0.001)
        }

    @Test
    fun `loading profile shows error when month observation fails`() =
        runTest(scheduler) {
            val failingObserveMonthScheduleUseCase =
                object : ObserveMonthScheduleUseCase {
                    override fun invoke(month: YearMonth): Flow<List<ScheduledShift>> = flow { error("schedule unavailable") }
                }

            val failingViewModel =
                ProfileViewModel(
                    dispatcherProvider = dispatcherProvider,
                    observeMonthScheduleUseCase = failingObserveMonthScheduleUseCase,
                    observeHospitalsUseCase = observeHospitalsUseCase,
                )

            failingViewModel.state.test {
                assertTrue(awaitItem().view is ProfileState.Loading)
                advanceUntilIdle()
                awaitItem() // InitLoaded
                assertTrue(awaitItem().view is ProfileState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
