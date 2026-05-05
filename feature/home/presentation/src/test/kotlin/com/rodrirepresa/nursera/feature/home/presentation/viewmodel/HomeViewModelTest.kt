package com.rodrirepresa.nursera.feature.home.presentation.viewmodel

import app.cash.turbine.test
import com.rodrirepresa.nursera.core.common.DispatcherProvider
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
class HomeViewModelTest {
    private val scheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(scheduler)

    private val dispatcherProvider =
        object : DispatcherProvider {
            override fun default() = testDispatcher

            override fun io() = testDispatcher

            override fun main() = testDispatcher
        }

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel(dispatcherProvider)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- HomeViewModel ---

    @Test
    fun `initial state is Loading`() =
        runTest(scheduler) {
            viewModel.state.test {
                assertTrue(awaitItem().view is HomeState.Loading)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `after load completes state transitions to Loaded`() =
        runTest(scheduler) {
            viewModel.state.test {
                assertTrue(awaitItem().view is HomeState.Loading)
                viewModel.execute(HomeIntent.Load)
                advanceUntilIdle()
                assertTrue(awaitItem().view is HomeState.Loaded)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `sending Load intent again while already Loaded keeps Loaded state`() =
        runTest(scheduler) {
            viewModel.state.test {
                assertTrue(awaitItem().view is HomeState.Loading)
                viewModel.execute(HomeIntent.Load)
                advanceUntilIdle()
                assertTrue(awaitItem().view is HomeState.Loaded)
                viewModel.execute(HomeIntent.Load)
                advanceUntilIdle()
                expectNoEvents()
                cancelAndIgnoreRemainingEvents()
            }
        }
}
