# Nursera — Android Project

## Overview
**Nursera** is a productivity Android app to help freelancers track their work sessions, clients, and projects.

> This file is the source of truth for conventions, architecture, and stack decisions.
> Update it whenever a significant decision is made. Ask Claude to update it at the end of important sessions.

---

## Architecture

### Pattern: Clean Architecture + MVI

Each feature module follows strict layer separation:

```
feature/
└── feature-x/
    ├── data/
    │   ├── datasource/        # Remote & local data sources
    │   ├── repository/        # Repository implementations
    │   └── model/             # Data Transfer Objects (DTOs)
    ├── domain/
    │   ├── model/             # Domain entities
    │   ├── repository/        # Repository interfaces
    │   └── usecase/           # One class per use case
    └── presentation/
        ├── navigation/
        │   └── [Feature]Navigation.kt  # Public entry-point — exposes [Feature]Route(), keeps Screen internal
        ├── ui/
        │   └── [Feature]Screen.kt      # internal Composable — owns ViewModel wiring
        └── viewmodel/
            ├── [Feature]ViewModel.kt
            ├── [Feature]State.kt
            ├── [Feature]Intent.kt
            ├── [Feature]SideEffect.kt
            └── [Feature]Transform.kt
```

### MVI Conventions

Every screen has the following classes:

```kotlin
// State — the full UI state
sealed interface [Feature]State : LoggableState {
    data object Loaded : [Feature]State
    data object Loading : [Feature]State
    data object Error : [Feature]State
}

// Intent — user actions / events
sealed interface [Feature]Intent : Intent {
    data object Load : [Feature]Intent
}

// SideEffect — one-time side effects (navigation, toasts)
sealed class [Feature]SideEffect {
    data object NavigateToSummary : [Feature]SideEffect
    data class ShowError(val message: String) : [Feature]SideEffect
}

// ViewModel — logic
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

// Transform — machine state mutation
internal object [Feature]Transform {

    object Loaded : ViewTransform<[Feature]State, [Feature]SideEffect>() {
        override fun mutate(currentState: [Feature]State): [Feature]State {
            return [Feature]State.Loaded
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

//Screen - represents the UI

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
            }
        }
    }
}

private fun consumeSideEffects(sideEffect: [Feature]SideEffect) {}

```

ViewModels expose:
- `state: StateFlow<State<XState, XSideEffect>>`
- `execute(intent: XIntent)` function

### Screen visibility rule

- The `[Feature]Screen` composable is always `internal`.
- Each ui module exposes one `public` `[Feature]Route()` composable in `[Feature]Navigation.kt`.
- `:app` only ever imports `[Feature]Route()`, never the Screen or ViewModel directly.

---

## Module Structure

```
Nursera/
├── app/                        # App module — DI graph root, MainActivity
├── core/
│   └── common/                 # Shared utilities: DispatcherProvider
└── feature/
    ├── [Feature]/
    │   ├── data/              # nursera.kotlin.library
    │   ├── domain/            # nursera.kotlin.library
    │   └── presentation/      # nursera.android.library.compose + nursera.android.library.hilt
    ├── favorites/
    │   ├── data/              # nursera.kotlin.library
    │   ├── domain/            # nursera.kotlin.library
    │   └── presentation/      # nursera.android.library.compose
    └── profile/
        ├── data/              # nursera.kotlin.library
        ├── domain/            # nursera.kotlin.library
        └── presentation/      # nursera.android.library.compose
```

> New features always get their own module under `feature/feature-x/`. Never add feature logic to `app/` or `core/`.

---

## Tech Stack

| Layer | Library | Version | Notes |
|---|---|---|---|
| UI | Jetpack Compose | BOM 2024.09.00 | No XML Views |
| ViewModel | Adidas MVI | 1.9.5 | `MviHost` + `Reducer` pattern |
| MVI Compose | adidas mvi-compose | 0.3.0 | `MviContainer` in screens |
| DI | Hilt | 2.52 | All ViewModels use `@HiltViewModel` |
| DI codegen | KSP | 2.0.21-1.0.25 | Replaces kapt |
| Async | Kotlin Coroutines + Flow | 1.8.1 | No RxJava |
| Navigation | (to be decided) | — | Add here when chosen |
| Networking | (to be added) | — | Add here when chosen |
| Local DB | (to be added) | — | Add here when chosen |

> When a new library is added to the project, update this table immediately.

---

## Dependency Injection (Hilt)

- `NurseraApplication` is annotated with `@HiltAndroidApp`.
- `MainActivity` is annotated with `@AndroidEntryPoint`.
- `AppModule` in `:app` provides app-wide singletons (e.g. `DispatcherProvider`).
- Each feature ui module adds Hilt + KSP plugins and declares its own `@HiltViewModel`.
- Scopes: `@Singleton` for repositories and data sources, `@ViewModelScoped` for use cases if stateful.
- Never inject `Context` directly — use `@ApplicationContext` where needed.

---

## DispatcherProvider

Defined in `:core:common`. Injected into every ViewModel via constructor injection.

```kotlin
interface DispatcherProvider {
    fun main(): CoroutineDispatcher = Dispatchers.Main
    fun default(): CoroutineDispatcher = Dispatchers.Default
    fun io(): CoroutineDispatcher = Dispatchers.IO
    fun unconfined(): CoroutineDispatcher = Dispatchers.Unconfined
}

object DefaultDispatcherProvider : DispatcherProvider
```

In tests, override with a fake that returns `StandardTestDispatcher` or `UnconfinedTestDispatcher`.

---

## Coding Conventions

### Naming
- **Files**: match the class name exactly — `[Feature]ViewModel.kt`, `[Feature]Screen.kt`
- **Public entry-point**: `[Feature]Route()` in `[Feature]Navigation.kt`
- **MVI State**: `[Feature]State`
- **Intents**: `[Feature]Intent`
- **SideEffects**: `[Feature]SideEffect`
- **Transform**: `[Feature]Transform`
- **Use cases**: verb + noun — `StartWorkSessionUseCase`, `GetClientListUseCase`
- **Repositories (interface)**: `[Feature]Repository` — lives in `:domain`
- **Repositories (impl)**: `[Feature]RepositoryImpl` — lives in `:data`

### Kotlin Style
- Prefer `data class` over plain class for models
- Use `sealed interface` for State and Intent, `sealed class` for SideEffect
- No nullable types unless strictly necessary — use empty defaults
- All coroutines launched from ViewModel via `viewModelScope`
- Use `@Immutable` on Compose UI state classes

### Compose
- One screen = one `internal` `[Feature]Screen` composable + one public `[Feature]Route`
- Always provide `@Preview` for every screen
- Screens are wired to the ViewModel internally; `[Feature]Route` takes no parameters
- Use `MviContainer` from `mvi-compose` to connect state and side effects

---

## Gradle Conventions

### `dependencies {}` block sections

Every `build.gradle.kts` must organise its `dependencies {}` block with the following sections, in this order (omit sections that are empty):

```kotlin
dependencies {

    // Projects
    implementation(projects.feature.[Feature].domain)

    // Libraries
    implementation(libs.androidx.core.ktx)

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Test
    testImplementation(libs.junit)
}
```

- **Projects** — internal module dependencies (`projects.*`)
- **Libraries** — external dependencies (`libs.*`)
- **Debug** — debug-only dependencies (`debugImplementation`)
- **Test** — unit and instrumented test dependencies (`testImplementation`, `androidTestImplementation`)

### Convention plugins

All modules use convention plugins from `build-logic/convention`. Never repeat `compileSdk`, `minSdk`, `compileOptions`, or `jvmTarget` in a module's `build.gradle.kts`.

| Plugin | Use when |
|---|---|
| `nursera.kotlin.library` | Pure Kotlin — no Android APIs (domain, data, core utilities) |
| `nursera.android.library` | Needs Android APIs but no Compose |
| `nursera.android.library.compose` | Needs Compose |
| `nursera.android.library.hilt` | Needs Hilt + KSP |
| `nursera.android.application` | The `:app` module only |

**Rule: prefer `nursera.kotlin.library` by default. Only use an Android plugin if the module imports Android APIs.**

A pure Kotlin module has no `AndroidManifest.xml` and no `android {}` block.

### Project dependencies

Always use type-safe project accessors (`projects.*`) — never `project(":some:module")`.

---

## Testing Strategy

- **Unit tests**: all ViewModels and Transforms (JUnit5 + Turbine for Flows)
- **Test dispatcher**: inject `StandardTestDispatcher` via fake `DispatcherProvider`; use `advanceUntilIdle()` to drive coroutines
- **Integration tests**: repository implementations against fake data sources
- Test files live under `src/test/` mirroring the production package — `[Feature]ViewModelTest.kt` next to `[Feature]ViewModel.kt`
- One test file per ViewModel covers both the ViewModel and its Transform classes

---

## What Claude Should Always Do

- Follow Clean Architecture layer boundaries — never skip layers
- Always generate the full MVI triad (State, Intent, SideEffect, Transform) for new screens
- Keep `[Feature]Screen` internal; expose only `[Feature]Route()` publicly
- Write unit tests alongside any new ViewModel or use case
- Update this file when a new library or convention is introduced
- Ask before adding a new dependency — suggest alternatives if one already covers the need
- Prefer Kotlin idiomatic code — no Java-style patterns

## What Claude Should Never Do

- Add business logic to Composables
- Use `LiveData` — use `StateFlow` / `SharedFlow` only
- Create God-classes or God-modules
- Skip the domain layer to call data sources directly from ViewModels
- Use `GlobalScope` for coroutines
- Import `[Feature]Screen` or `[Feature]ViewModel` from `:app` — use `[Feature]Route()` only
