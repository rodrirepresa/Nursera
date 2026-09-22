# Testing conventions

Canonical example: `feature/hospital/presentation/src/test/.../EditHospitalViewModelTest.kt`.

## Scope

- Unit tests for every ViewModel and its Transforms (JUnit + Turbine).
- One test file per ViewModel — it covers both the ViewModel and the `[Feature]Transform` classes.
- Test files mirror the production package under `src/test/`.
- Integration tests: repository implementations against fake data sources.
- Use cases get their own test only when they contain real logic; skip pure one-line delegations.

## Setup

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class EditHospitalViewModelTest {

    private val scheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(scheduler)

    private val dispatcherProvider = object : DispatcherProvider {
        override fun default() = testDispatcher
        override fun io() = testDispatcher
        override fun main() = testDispatcher
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
```

- Inject `StandardTestDispatcher` through a fake `DispatcherProvider`; drive it with `advanceUntilIdle()`.
- Route args: build `SavedStateHandle(mapOf("hospitalId" to id.toString()))` and pass it directly
  to the ViewModel constructor.
- Remember the ViewModel fires `Load` from `init {}`, so state may already have advanced past
  `Loading` by the time you collect.

## Assertions

```kotlin
@Test
fun `load emits Loaded`() = runTest(scheduler) {
    viewModel.state.test {
        assertTrue(awaitItem().view is EditHospitalState.Loading)
        advanceUntilIdle()
        assertTrue(awaitItem().view is EditHospitalState.Loaded)
        cancelAndIgnoreRemainingEvents()
    }
}

@Test
fun `ShowError transform maps any state to Error`() {
    assertEquals(EditHospitalState.Error, EditHospitalTransform.ShowError.mutate(EditHospitalState.Loading))
}
```

Transforms are pure — test them directly, without going through the ViewModel.

## Running

```bash
./gradlew :feature:hospital:presentation:testDebugUnitTest   # prefer targeted
./gradlew testDebugUnitTest
```
