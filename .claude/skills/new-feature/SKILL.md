---
description: Scaffold a new feature module for the Nursera project following CLAUDE.md conventions. Creates data, domain and presentation modules with full MVI boilerplate and unit tests.
---

Scaffold a complete new feature called **$ARGUMENTS** following the Nursera CLAUDE.md conventions exactly.

Use `$ARGUMENTS` as the feature name in lowercase (e.g. `tracking`).
Derive the capitalized class prefix from it (e.g. `tracking` → `Tracking`).

The steps below are exhaustive — execute all of them in order.

---

## 1. Register modules in `settings.gradle.kts`

Add these three lines alongside the existing `include` entries:

```kotlin
include(":feature:$ARGUMENTS:data")
include(":feature:$ARGUMENTS:domain")
include(":feature:$ARGUMENTS:presentation")
```

---

## 2. Create `feature/$ARGUMENTS/domain/build.gradle.kts`

```kotlin
plugins {
    id("nursera.kotlin.library")
}
```

---

## 3. Create `feature/$ARGUMENTS/data/build.gradle.kts`

```kotlin
plugins {
    id("nursera.kotlin.library")
}

dependencies {

    // Projects
    implementation(projects.feature.$ARGUMENTS.domain)
}
```

---

## 4. Create `feature/$ARGUMENTS/presentation/build.gradle.kts`

```kotlin
plugins {
    id("nursera.android.library.compose")
    id("nursera.android.library.hilt")
}

android {
    namespace = "com.rodrirepresa.nursera.feature.$ARGUMENTS.presentation"
}

dependencies {

    // Projects
    implementation(projects.feature.$ARGUMENTS.domain)
    implementation(projects.core.common)

    // Libraries
    implementation(libs.adidas.mvi)
    implementation(libs.adidas.mvi.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Test
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.androidx.arch.core.testing)
}
```

---

## 6. Create domain layer

**`feature/$ARGUMENTS/domain/src/main/kotlin/com/rodrirepresa/nursera/feature/$ARGUMENTS/domain/[Feature]Repository.kt`**

```kotlin
package com.rodrirepresa.nursera.feature.$ARGUMENTS.domain

interface [Feature]Repository
```

---

## 7. Create data layer

**`feature/$ARGUMENTS/data/src/main/kotlin/com/rodrirepresa/nursera/feature/$ARGUMENTS/data/[Feature]RepositoryImpl.kt`**

```kotlin
package com.rodrirepresa.nursera.feature.$ARGUMENTS.data

import com.rodrirepresa.nursera.feature.$ARGUMENTS.domain.[Feature]Repository

class [Feature]RepositoryImpl : [Feature]Repository
```

---

## 8. Create presentation/viewmodel — State

**`feature/$ARGUMENTS/presentation/src/main/kotlin/com/rodrirepresa/nursera/feature/$ARGUMENTS/presentation/viewmodel/[Feature]State.kt`**

```kotlin
package com.rodrirepresa.nursera.feature.$ARGUMENTS.presentation.viewmodel

import com.adidas.mvi.LoggableState

sealed interface [Feature]State : LoggableState {
    data object Loading : [Feature]State
    data object Loaded : [Feature]State
    data object Error : [Feature]State
}
```

---

## 9. Create presentation/viewmodel — Intent

**`feature/$ARGUMENTS/presentation/src/main/kotlin/com/rodrirepresa/nursera/feature/$ARGUMENTS/presentation/viewmodel/[Feature]Intent.kt`**

```kotlin
package com.rodrirepresa.nursera.feature.$ARGUMENTS.presentation.viewmodel

import com.adidas.mvi.Intent

sealed interface [Feature]Intent : Intent {
    data object Load : [Feature]Intent
}
```

---

## 10. Create presentation/viewmodel — SideEffect

**`feature/$ARGUMENTS/presentation/src/main/kotlin/com/rodrirepresa/nursera/feature/$ARGUMENTS/presentation/viewmodel/[Feature]SideEffect.kt`**

```kotlin
package com.rodrirepresa.nursera.feature.$ARGUMENTS.presentation.viewmodel

internal sealed class [Feature]SideEffect
```

---

## 11. Create presentation/viewmodel — Transform

**`feature/$ARGUMENTS/presentation/src/main/kotlin/com/rodrirepresa/nursera/feature/$ARGUMENTS/presentation/viewmodel/[Feature]Transform.kt`**

```kotlin
package com.rodrirepresa.nursera.feature.$ARGUMENTS.presentation.viewmodel

import com.adidas.mvi.sideeffects.SideEffects
import com.adidas.mvi.transform.SideEffectTransform
import com.adidas.mvi.transform.ViewTransform

internal object [Feature]Transform {

    object Loaded : ViewTransform<[Feature]State, [Feature]SideEffect>() {
        override fun mutate(currentState: [Feature]State): [Feature]State {
            return [Feature]State.Loaded
        }
    }

    object Error : ViewTransform<[Feature]State, [Feature]SideEffect>() {
        override fun mutate(currentState: [Feature]State): [Feature]State {
            return [Feature]State.Error
        }
    }

    data class AddSideEffect(
        val sideEffect: [Feature]SideEffect,
    ) : SideEffectTransform<[Feature]State, [Feature]SideEffect>() {
        override fun mutate(sideEffects: SideEffects<[Feature]SideEffect>): SideEffects<[Feature]SideEffect> {
            return sideEffects.add(sideEffect)
        }
    }
}
```

---

## 12. Create presentation/viewmodel — ViewModel

**`feature/$ARGUMENTS/presentation/src/main/kotlin/com/rodrirepresa/nursera/feature/$ARGUMENTS/presentation/viewmodel/[Feature]ViewModel.kt`**

```kotlin
package com.rodrirepresa.nursera.feature.$ARGUMENTS.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adidas.mvi.MviHost
import com.adidas.mvi.Reducer
import com.adidas.mvi.State
import com.adidas.mvi.transform.StateTransform
import com.rodrirepresa.nursera.core.common.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
internal class [Feature]ViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel(), MviHost<[Feature]Intent, State<[Feature]State, [Feature]SideEffect>> {

    private val reducer: Reducer<[Feature]Intent, State<[Feature]State, [Feature]SideEffect>> = com.adidas.mvi.reducer.Reducer(
        coroutineScope = viewModelScope,
        defaultDispatcher = dispatcherProvider.default(),
        initialInnerState = [Feature]State.Loading,
        intentExecutor = this::executeIntent,
    )

    override val state: StateFlow<State<[Feature]State, [Feature]SideEffect>> = reducer.state

    override fun execute(intent: [Feature]Intent) {
        reducer.executeIntent(intent)
    }

    private fun executeIntent(intent: [Feature]Intent): Flow<StateTransform<State<[Feature]State, [Feature]SideEffect>>> =
        when (intent) {
            is [Feature]Intent.Load -> executeLoad()
        }

    private fun executeLoad() = flow {
        emit([Feature]Transform.Loaded)
    }
}
```

---

## 13. Create presentation/ui — Screen

**`feature/$ARGUMENTS/presentation/src/main/kotlin/com/rodrirepresa/nursera/feature/$ARGUMENTS/presentation/ui/[Feature]Screen.kt`**

```kotlin
package com.rodrirepresa.nursera.feature.$ARGUMENTS.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.adidas.mvi.compose.MviContainer
import com.rodrirepresa.nursera.feature.$ARGUMENTS.presentation.viewmodel.[Feature]Intent
import com.rodrirepresa.nursera.feature.$ARGUMENTS.presentation.viewmodel.[Feature]SideEffect
import com.rodrirepresa.nursera.feature.$ARGUMENTS.presentation.viewmodel.[Feature]State
import com.rodrirepresa.nursera.feature.$ARGUMENTS.presentation.viewmodel.[Feature]ViewModel

@Composable
internal fun [Feature]Screen(
    viewModel: [Feature]ViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.execute([Feature]Intent.Load)
    }

    MviContainer(
        state = viewModel.state,
        onSideEffect = { sideEffect ->
            consumeSideEffects(sideEffect)
        },
    ) { state ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                is [Feature]State.Loading -> Text("Loading")
                is [Feature]State.Loaded -> Text("Loaded")
                is [Feature]State.Error -> Text("Error")
            }
        }
    }
}

private fun consumeSideEffects(sideEffect: [Feature]SideEffect) {}
```

---

## 14. Create presentation/navigation — Route

**`feature/$ARGUMENTS/presentation/src/main/kotlin/com/rodrirepresa/nursera/feature/$ARGUMENTS/presentation/navigation/[Feature]Navigation.kt`**

```kotlin
package com.rodrirepresa.nursera.feature.$ARGUMENTS.presentation.navigation

import androidx.compose.runtime.Composable
import com.rodrirepresa.nursera.feature.$ARGUMENTS.presentation.ui.[Feature]Screen

@Composable
fun [Feature]Route() {
    [Feature]Screen()
}
```

---

## 15. Create unit test

**`feature/$ARGUMENTS/presentation/src/test/kotlin/com/rodrirepresa/nursera/feature/$ARGUMENTS/presentation/viewmodel/[Feature]ViewModelTest.kt`**

```kotlin
package com.rodrirepresa.nursera.feature.$ARGUMENTS.presentation.viewmodel

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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class [Feature]ViewModelTest {

    private val scheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(scheduler)

    private val dispatcherProvider = object : DispatcherProvider {
        override fun default() = testDispatcher
        override fun io() = testDispatcher
        override fun main() = testDispatcher
    }

    private lateinit var viewModel: [Feature]ViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = [Feature]ViewModel(dispatcherProvider)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- [Feature]ViewModel ---

    @Test
    fun `initial state is Loading`() = runTest(scheduler) {
        viewModel.state.test {
            assertTrue(awaitItem().view is [Feature]State.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `after load completes state transitions to Loaded`() = runTest(scheduler) {
        viewModel.state.test {
            assertTrue(awaitItem().view is [Feature]State.Loading)
            viewModel.execute([Feature]Intent.Load)
            advanceUntilIdle()
            assertTrue(awaitItem().view is [Feature]State.Loaded)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sending Load intent again while already Loaded keeps Loaded state`() = runTest(scheduler) {
        viewModel.state.test {
            assertTrue(awaitItem().view is [Feature]State.Loading)
            viewModel.execute([Feature]Intent.Load)
            advanceUntilIdle()
            assertTrue(awaitItem().view is [Feature]State.Loaded)
            viewModel.execute([Feature]Intent.Load)
            advanceUntilIdle()
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- [Feature]Transform ---

    @Test
    fun `Loaded transform mutates Loading to Loaded`() {
        val result = [Feature]Transform.Loaded.mutate([Feature]State.Loading)
        assertEquals([Feature]State.Loaded, result)
    }

    @Test
    fun `Loaded transform is idempotent`() {
        val result = [Feature]Transform.Loaded.mutate([Feature]State.Loaded)
        assertEquals([Feature]State.Loaded, result)
    }
}
```

---

## Done

After creating all files, tell the user:
- All files created for feature `$ARGUMENTS`
- Sync Gradle to pick up the new modules
- If this feature needs a nav bar entry, add it manually to `AppDestinations` in `MainActivity.kt` and import `[Feature]Route()`
- Update if needed root CLAUDE.md
