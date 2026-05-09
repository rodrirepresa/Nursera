package com.rodrirepresa.nursera.feature.hospital.presentation.viewmodel

import app.cash.turbine.test
import com.rodrirepresa.nursera.core.common.DispatcherProvider
import com.rodrirepresa.nursera.feature.hospital.domain.model.Hospital
import com.rodrirepresa.nursera.feature.hospital.domain.model.ShiftType
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.DeleteHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.ObserveHospitalsUseCase
import com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel.HospitalIntent
import com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel.HospitalState
import com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel.HospitalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
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
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class HospitalViewModelTest {
    private val scheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(scheduler)

    private val dispatcherProvider =
        object : DispatcherProvider {
            override fun default() = testDispatcher

            override fun io() = testDispatcher

            override fun main() = testDispatcher
        }

    private val hospitalsFlow = MutableStateFlow<List<Hospital>>(emptyList())

    private val observeHospitals =
        object : ObserveHospitalsUseCase {
            override fun invoke(): Flow<List<Hospital>> = hospitalsFlow
        }

    private val deleteHospital =
        object : DeleteHospitalUseCase {
            override suspend fun invoke(id: UUID) {
                hospitalsFlow.update { current -> current.filter { it.id != id } }
            }
        }

    private lateinit var viewModel: HospitalViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HospitalViewModel(dispatcherProvider, observeHospitals, deleteHospital)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() =
        runTest(scheduler) {
            viewModel.state.test {
                assertTrue(awaitItem().view is HospitalState.Loading)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `after load state transitions to Loaded with hospitals`() =
        runTest(scheduler) {
            val hospital = makeHospital()
            hospitalsFlow.value = listOf(hospital)

            viewModel.state.test {
                assertTrue(awaitItem().view is HospitalState.Loading)
                viewModel.execute(HospitalIntent.Load)
                advanceUntilIdle()
                val loaded = awaitItem().view as HospitalState.Loaded
                assertEquals(1, loaded.hospitals.size)
                assertEquals(hospital.name, loaded.hospitals.first().name)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `delete intent removes hospital from state`() =
        runTest(scheduler) {
            val hospital = makeHospital()
            hospitalsFlow.value = listOf(hospital)

            viewModel.state.test {
                assertTrue(awaitItem().view is HospitalState.Loading)
                viewModel.execute(HospitalIntent.Load)
                advanceUntilIdle()
                val loaded = awaitItem().view as HospitalState.Loaded
                assertEquals(1, loaded.hospitals.size)

                viewModel.execute(HospitalIntent.OpenHospitalDetail(hospital.id))
                advanceUntilIdle()
                val afterDelete = awaitItem().view as HospitalState.Loaded
                assertTrue(afterDelete.hospitals.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun makeHospital() =
        Hospital(
            id = UUID.randomUUID(),
            name = "Test Hospital",
            color = 0xFF1565C0.toInt(),
            irpf = 15.0f,
            shifts =
                listOf(
                    ShiftType(UUID.randomUUID(), "Mañana", LocalTime.of(8, 0), LocalTime.of(15, 0), 18.50),
                ),
        )
}
