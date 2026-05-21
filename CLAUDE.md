# Nursera — Android Project

## Overview
**Nursera** is a productivity Android app designed for nurses in Spain who work across multiple hospitals simultaneously.
Many Spanish nurses split their working hours between different hospitals, resulting in variable monthly income that is hard to predict. Nursera solves this by letting nurses track their shifts per hospital and get notified the moment they hit their personal income target for the month.
Core use case
A nurse sets up her hospitals — each with its own pay rates per shift type (morning, afternoon, night, weekend, holiday). Every time she adds a shift to her monthly calendar, Nursera recalculates her projected net income. When the total crosses her personal target, she gets notified: she has earned enough and can stop taking extra shifts.

> This file is the source of truth for conventions, architecture, and stack decisions.
> Update it whenever a significant decision is made. Ask Claude to update it at the end of important sessions.

---

## Architecture

### Pattern: Clean Architecture + MVI

Each feature module follows strict layer separation:

```
feature/
└── hospital/
    ├── data/
    │   ├── datasource/        # Remote & local data sources
    │   ├── repository/        # Repository implementations
    │   └── model/             # Data Transfer Objects (DTOs)
    ├── domain/
    │   ├── model/             # Domain entities
    │   ├── repository/        # Repository interfaces
    │   └── usecase/           # One class per use case
    └── presentation/
        ├── create/
        │   ├── model/         # @Immutable form-state models shared across sub-screens
        │   ├── ui/            # internal CreateHospitalScreen composable
        │   └── viewmodel/     # ViewModel, State, Intent, SideEffect, Transform
        ├── edit/
        │   ├── mappers/       # internal extension funs: DomainModel.toUiModel()
        │   ├── ui/            # internal EditHospitalScreen composable
        │   ├── validators/    # Pure input-validation extension functions
        │   └── viewmodel/     # ViewModel, State, Intent, SideEffect, Transform + UI display models
        └── list/
            ├── navigation/    # NavGraph, @Serializable route objects/classes
            ├── ui/            # internal HospitalScreen + sub-composables
            └── viewmodel/     # ViewModel, State, Intent, SideEffect, Transform
```

For single-screen features the flat structure applies:

```
presentation/
├── navigation/
│   └── [Feature]Navigation.kt
├── ui/
│   └── [Feature]Screen.kt
└── viewmodel/
    ├── [Feature]ViewModel.kt
    ├── [Feature]State.kt
    ├── [Feature]Intent.kt
    ├── [Feature]SideEffect.kt
    └── [Feature]Transform.kt
```

---

### MVI Conventions

#### State

```kotlin
sealed interface EditHospitalState : LoggableState {
    data object Loading : EditHospitalState
    data object Error : EditHospitalState

    data class Loaded(
        val hospitalId: UUID,
        val hospitalName: String = "",
        val hospitalColor: Int = 0,
        val originalIrpf: String = "",
        val irpf: String = "",
        val irpfError: String? = null,
        val shifts: ImmutableList<ShiftItem> = persistentListOf(),
    ) : EditHospitalState
}
```

- Always `sealed interface` extending `LoggableState`
- `Loading` and `Error` are `data object`; `Loaded` is a `data class`
- `Loaded` uses `ImmutableList` (from `kotlinx.collections.immutable`) for any list fields — never `List`
- Inline validation error fields (`irpfError: String?`) live inside `Loaded`, not as a separate state
- No `isSaving: Boolean` at the top `Loaded` level when saving is scoped to a sub-item — put it on the relevant item model instead (see `ShiftItem.Form`)

#### Intent

```kotlin
sealed interface EditHospitalIntent : Intent {
    data object Load : EditHospitalIntent
    data object NavigateBack : EditHospitalIntent
    data class UpdateIrpf(val value: String) : EditHospitalIntent
    data object ShowForm : EditHospitalIntent
    data class UpdateNewShift(val shift: ShiftFormUiState) : EditHospitalIntent
    data class ToggleExistingShiftSelection(val shiftId: UUID) : EditHospitalIntent
    data object DeleteSelectedShifts : EditHospitalIntent
    data object SaveShift : EditHospitalIntent
}
```

- Always `sealed interface` extending `Intent`
- Parameter-less intents → `data object`; intents carrying data → `data class`
- Navigation intents (e.g. `NavigateBack`) belong here — the ViewModel converts them to a `SideEffect`

#### SideEffect

```kotlin
sealed class EditHospitalSideEffect {
    data object NavigateBack : EditHospitalSideEffect()
}
```

- Always `sealed class` (not interface)
- One-time events only: navigation, toasts — never state that belongs in `Loaded`

#### Transform

```kotlin
internal object EditHospitalTransform {

    // Stateless transforms → object
    object ShowError : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState = EditHospitalState.Error
    }

    // Stateful transforms → data class
    data class UpdateIrpf(
        val irpf: String,
    ) : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState {
            if (currentState !is EditHospitalState.Loaded) return currentState
            return currentState.copy(irpf = irpf, irpfError = validateIrpf(irpf))
        }
    }

    // Side-effect transform — always a data class named AddSideEffect
    data class AddSideEffect(
        val sideEffect: EditHospitalSideEffect,
    ) : SideEffectTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(sideEffects: SideEffects<EditHospitalSideEffect>): SideEffects<EditHospitalSideEffect> =
            sideEffects.add(sideEffect)
    }
}
```

- Top-level `internal object [Feature]Transform`
- Stateless transforms → `object`; stateful (carry data) → `data class`
- Always guard with `if (currentState !is EditHospitalState.Loaded) return currentState`
- Use `.toPersistentList()` when producing a new list from a mapped collection

#### ViewModel

```kotlin
@HiltViewModel
internal class EditHospitalViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val addShiftToHospitalUseCase: AddShiftToHospitalUseCase,
    private val observeHospitalByIdUseCase: ObserveHospitalByIdUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel(), MviHost<EditHospitalIntent, State<EditHospitalState, EditHospitalSideEffect>> {

    private val hospitalId: UUID = UUID.fromString(savedStateHandle.toRoute<EditHospital>().hospitalId)

    private val reducer = com.adidas.mvi.reducer.Reducer(
        coroutineScope = viewModelScope,
        defaultDispatcher = dispatcherProvider.default(),
        initialInnerState = EditHospitalState.Loading,
        intentExecutor = this::executeIntent,
    )

    override val state: StateFlow<State<EditHospitalState, EditHospitalSideEffect>> = reducer.state

    init { execute(EditHospitalIntent.Load) }

    override fun execute(intent: EditHospitalIntent) { reducer.executeIntent(intent) }

    private fun executeIntent(
        intent: EditHospitalIntent,
    ): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
        when (intent) {
            is EditHospitalIntent.Load -> executeLoad(hospitalId)
            is EditHospitalIntent.NavigateBack -> executeAddSideEffect(EditHospitalSideEffect.NavigateBack)
            is EditHospitalIntent.UpdateIrpf -> executeUpdateIrpf(intent.value)
            is EditHospitalIntent.SaveShift -> executeSaveShift()
            // …
        }

    // Each intent gets its own private execute* function returning Flow<StateTransform<…>>
    private fun executeSaveShift(): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
        flow {
            val loadedState = state.value.view as? EditHospitalState.Loaded ?: return@flow
            // … business logic …
            emit(EditHospitalTransform.SetFormVisible(false))
            emit(EditHospitalTransform.SetFormSaving(false))
        }.onStart {
            emit(EditHospitalTransform.SetFormSaving(true)) // emit before the operation starts
        }

    private fun executeAddSideEffect(
        sideEffect: EditHospitalSideEffect,
    ): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
        flow { emit(EditHospitalTransform.AddSideEffect(sideEffect)) }
}
```

Rules:
- `@HiltViewModel` + single-line `@Inject constructor`
- Route args read via `savedStateHandle.toRoute<RouteClass>()` — route class must be `internal` so the ViewModel can reference it
- One private `execute[IntentName]()` per intent; each returns `Flow<StateTransform<…>>`
- Use `.onStart { emit(…) }` to emit a "saving started" transform before the async operation
- Read current state synchronously via `state.value.view as? [Feature]State.Loaded`
- Use `catch { emit(EditHospitalTransform.ShowError) }` on observable flows
- **No extra class-level properties** — the ViewModel body contains only `reducer`, `state`, `init`, `execute`, and private `execute*` functions. Any derived value (e.g. a route arg UUID) may be stored as a `private val` computed once from constructor params, but mutable fields (`MutableStateFlow`, `MutableSharedFlow`, etc.) are forbidden.

#### Screen

```kotlin
@Composable
internal fun EditHospitalScreen(
    onNavigateBack: () -> Unit,                        // navigation callbacks injected at this level only
    viewModel: EditHospitalViewModel = hiltViewModel(),
) {
    MviContainer(
        state = viewModel.state,
        onSideEffect = { sideEffect ->
            when (sideEffect) {
                is EditHospitalSideEffect.NavigateBack -> onNavigateBack()
            }
        },
    ) { state ->
        when (state) {
            is EditHospitalState.Loading -> NurseraLoadingView()
            is EditHospitalState.Error -> NurseraErrorView(
                message = stringResource(R.string.edit_hospital_error_message),
            )
            is EditHospitalState.Loaded -> EditHospitalLoadedContent(
                state = state,
                executeIntent = viewModel::execute,
            )
        }
    }
}

@Composable
private fun EditHospitalLoadedContent(
    state: EditHospitalState.Loaded,
    executeIntent: (EditHospitalIntent) -> Unit,
    modifier: Modifier = Modifier,
) { /* … */ }
```

Rules:
- The Screen composable is `internal` and receives navigation lambdas (`onNavigateBack`, `navigateTo*`) — these are wired **only** inside `onSideEffect {}`, never passed to child composables
- Loaded-content composables receive a single `executeIntent: ([Feature]Intent) -> Unit` lambda
- `[Feature]LoadedContent` is always `private`

---

### Init vs LaunchedEffect for initial load

- The initial `Load` intent fires from `init {}` inside the ViewModel — never from a `LaunchedEffect` in the Screen
- For screens with route arguments inject `SavedStateHandle` and read via `savedStateHandle.toRoute<RouteClass>()`
- In tests: `SavedStateHandle(mapOf("argName" to value))` passed directly to the constructor

---

### Lazy list items with mixed content (`ShiftItem` pattern)

When a lazy list shows heterogeneous items (existing items + an inline form), model them as a sealed interface with a stable `listKey`:

```kotlin
@Immutable
sealed interface ShiftItem {
    val listKey: String

    @Immutable
    data class Existing(
        val model: ShiftUiModel,
        val isSelected: Boolean = false,
    ) : ShiftItem {
        override val listKey: String get() = model.id.toString()
    }

    @Immutable
    data class Form(
        val form: ShiftFormUiState = ShiftFormUiState(),
        val canSave: Boolean = false,
        val isSaving: Boolean = false,    // saving state lives here, not in the top Loaded state
        val isVisible: Boolean = false,
    ) : ShiftItem {
        override val listKey: String get() = "form"
    }
}
```

- Annotate the sealed interface and each subtype with `@Immutable`
- Use `key = { it.listKey }` in `items(…)` for stable animations
- The form item always exists in the list; show/hide it with `isVisible` + `AnimatedVisibility`

---

## Module Structure

```
Nursera/
├── app/                        # App module — DI graph root, MainActivity
├── core/
│   ├── common/                 # Shared utilities: DispatcherProvider
│   └── ui/                     # Shared Compose design-system: NeoBrutalistCard, NeoBrutalistIconButton, NeoBrutalistChip, NurseraToolbar
└── feature/
    ├── hospital/
    │   ├── data/              # nursera.kotlin.library
    │   ├── domain/            # nursera.kotlin.library
    │   └── presentation/      # nursera.android.library.compose + nursera.android.library.hilt
    ├── schedule/
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
| Navigation | Navigation Compose | 2.8.3 | `NavHost` + type-safe `@Serializable` routes; nested graphs per feature |
| Serialization | kotlinx.serialization | 1.7.3 | Used for type-safe navigation route objects |
| Immutable collections | kotlinx.collections.immutable | — | `ImmutableList` / `persistentListOf()` for all list fields in State |
| Networking | (to be added) | — | Add here when chosen |
| Local DB | (to be added) | — | Add here when chosen |

> When a new library is added to the project, update this table immediately.

---

## Navigation Conventions

The app uses **Jetpack Navigation Compose 2.8.3** with type-safe routes (`@Serializable` objects/data classes).

### Structure

Each feature's `[Feature]Navigation.kt` (inside `list/navigation/` for multi-screen features) declares:
- A public `@Serializable object [Feature]Graph` — the nested graph root `:app` navigates to
- `private` route objects for internal destinations (e.g. `HospitalList`, `CreateHospital`)
- `internal` route data classes for destinations the ViewModel must reference via `SavedStateHandle` (e.g. `EditHospital`)
- A public `NavGraphBuilder.[feature]Graph(navController: NavController)` extension function

```kotlin
@Serializable object HospitalGraph          // public — referenced by :app for tab selection

@Serializable private object HospitalList   // private — only used inside the graph
@Serializable private object CreateHospital // private — only used inside the graph
@Serializable internal data class EditHospital(val hospitalId: String) // internal — ViewModel reads it

fun NavGraphBuilder.hospitalGraph(navController: NavController) {
    navigation<HospitalGraph>(startDestination = HospitalList) {
        composable<HospitalList> {
            HospitalScreen(
                navigateToCreateHospital = { navController.navigate(CreateHospital) },
                navigateToEditHospital = { id -> navController.navigate(EditHospital(id.toString())) },
            )
        }
        composable<CreateHospital> {
            CreateHospitalScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable<EditHospital> {
            EditHospitalScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
```

### Rules

- Route objects `:app` needs for tab selection are `public`; inner routes are `private`; routes the ViewModel reads are `internal`
- Never pass a `NavController` into a `@Composable` screen — pass typed callback lambdas instead
- Navigation callbacks are wired in `onSideEffect {}` inside the Screen — never passed to child composables
- Tab selection uses `NavDestination.hierarchy.hasRoute(KClass)` so the correct tab highlights even when inside a nested graph
- `NavigationSuiteScaffold` tab clicks use `popUpTo(findStartDestination().id) { saveState = true }` + `restoreState = true` to preserve tab back stacks

---

## Dependency Injection (Hilt)

- `NurseraApplication` is annotated with `@HiltAndroidApp`
- `MainActivity` is annotated with `@AndroidEntryPoint`
- `AppModule` in `:app` provides app-wide singletons (e.g. `DispatcherProvider`)
- Each feature ui module adds Hilt + KSP plugins and declares its own `@HiltViewModel`
- Scopes: `@Singleton` for repositories and data sources, `@ViewModelScoped` for use cases if stateful
- Never inject `Context` directly — use `@ApplicationContext` where needed

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
- **Public graph entry-point**: `NavGraphBuilder.[feature]Graph(navController)` in `[Feature]Navigation.kt`
- **MVI State**: `[Feature]State`
- **Intents**: `[Feature]Intent`
- **SideEffects**: `[Feature]SideEffect`
- **Transform**: `[Feature]Transform`
- **Use cases**: verb + noun — `AddShiftToHospitalUseCase`, `ObserveHospitalByIdUseCase`
- **Repositories (interface)**: `[Feature]Repository` — lives in `:domain`
- **Repositories (impl)**: `[Feature]RepositoryImpl` — lives in `:data`

### Kotlin Style
- Prefer `data class` over plain class for models
- Use `sealed interface` for State and Intent, `sealed class` for SideEffect
- No nullable types unless strictly necessary — use empty string defaults, not null
- All coroutines launched from ViewModel via `viewModelScope`
- Use `@Immutable` on all Compose UI model classes and sealed item interfaces

### UI Models

UI models come in two kinds:

| Kind | Location | Example |
|---|---|---|
| Display model (read-only) | `[screen]/viewmodel/` | `ShiftUiModel.kt` |
| Form state (editable) | `[screen]/model/` | `ShiftFormUiState.kt` |

- Both are `@Immutable data class` with pre-formatted strings (no `LocalTime`, `UUID`, or domain types)
- Mappers live in `[screen]/mappers/` as `internal` extension functions: `DomainModel.toUiModel()`
- Mapping happens in the ViewModel inside the Flow before emitting a Transform — never in a Composable
- Domain imports are only allowed in the ViewModel (use cases) and mapper files

### Validators

Input validation lives in a dedicated `[screen]/validators/` package as pure Kotlin extension functions:

```kotlin
// Returns null if valid, or a localised error string if invalid
internal fun validateIrpf(value: String): String? { … }

// Returns a copy with all error fields populated
internal fun ShiftFormUiState.withValidation(): ShiftFormUiState { … }

// Returns true when all error fields are null and required fields are non-blank
internal fun ShiftFormUiState.isValid(): Boolean { … }
```

- One file per validation concern (e.g. `IrpfValidator.kt`, `ShiftFormUiStateValidator.kt`, `TimeValidator.kt`)
- Always `internal` — never exported outside the presentation module
- Called from the ViewModel or Transform, never from a Composable

### Compose
- One screen = one `internal` `[Feature]Screen` composable
- `[Feature]LoadedContent` is always `private`
- Always provide `@Preview` for every screen state (Loading, Error, Loaded, Loaded+selection, Loaded+saving…)
- Use `MviContainer` from `mvi-compose` to connect state and side effects
- Use `NurseraLoadingView` and `NurseraErrorView` from `core/ui` for loading and error states
- `isSaving` for a sub-item operation belongs on the item model (e.g. `ShiftItem.Form.isSaving`), not on `Loaded`; use `NurseraCta(isSaving = …)` to reflect it

### Intent callbacks in Composables
- Loaded-content composables receive a single `executeIntent: ([Feature]Intent) -> Unit` lambda wired as `executeIntent = viewModel::execute`
- Navigation callbacks live only in the Screen; child composables fire intents, never receive nav lambdas
- The Screen wires nav callbacks exclusively inside `onSideEffect {}` in `MviContainer`

### Previews for `core/ui` components
- Every `public` `@Composable` in `core/ui` **must** have at least one `@Preview`
- Each distinct visual state (default, pressed, error, disabled) gets its own `@Preview` function
- Preview functions are `private` and live in the same file as the component

---

## Gradle Conventions

### `dependencies {}` block sections

Every `build.gradle.kts` must organise its `dependencies {}` block with the following sections, in this order (omit sections that are empty):

```kotlin
dependencies {

    // Projects
    implementation(projects.feature.hospital.domain)

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
- **SavedStateHandle**: construct `SavedStateHandle(mapOf("hospitalId" to id.toString()))` and pass directly to the ViewModel
- **Integration tests**: repository implementations against fake data sources
- Test files live under `src/test/` mirroring the production package — `[Feature]ViewModelTest.kt` next to `[Feature]ViewModel.kt`
- One test file per ViewModel covers both the ViewModel and its Transform classes

---

## What Claude Should Always Do

- Follow Clean Architecture layer boundaries — never skip layers
- Always generate the full MVI triad (State, Intent, SideEffect, Transform) for new screens
- Keep `[Feature]Screen` internal; expose only the `NavGraphBuilder.[feature]Graph()` extension publicly
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
- Import `[Feature]Screen` or `[Feature]ViewModel` from `:app` — use the graph extension function only
- Import domain models (`feature.*.domain.model.*`) in Composables, State, or Transform classes — map to UI models first
- Use `List` in State — always use `ImmutableList` with `persistentListOf()`
- Use the word **Cache** in Transform or Intent names — use descriptive domain names instead (e.g. `AddMonth`, `SetDisplayedMonth`)
