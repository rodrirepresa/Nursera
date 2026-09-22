# MVI conventions

Library: Adidas MVI 1.9.5 (`MviHost` + `Reducer`) with mvi-compose 0.3.0.
Canonical example: `feature/hospital/presentation/.../edit/viewmodel/`.

## Package layout

Multi-screen feature:

```text
presentation/
├── <screen>/
│   ├── model/        @Immutable editable form-state models
│   ├── mappers/      internal ext funs: DomainModel.toUiModel()
│   ├── validators/   pure validation ext funs
│   ├── ui/           internal <Screen>Screen + private sub-composables
│   └── viewmodel/    ViewModel, State, Intent, SideEffect, Transform, display UI models
└── <startScreen>/navigation/   NavGraph + @Serializable routes
```

Single-screen feature: flat `navigation/`, `ui/`, `viewmodel/`.

## State

```kotlin
sealed interface EditHospitalState : LoggableState {
    data object Loading : EditHospitalState
    data object Error : EditHospitalState

    data class Loaded(
        val hospitalId: UUID,
        val hospitalName: String = "",
        val irpf: String = "",
        val irpfError: String? = null,
        val shifts: ImmutableList<ShiftItem> = persistentListOf(),
    ) : EditHospitalState
}
```

- Always `sealed interface` extending `LoggableState`.
- `Loading` / `Error` are `data object`; `Loaded` is a `data class`.
- Lists are `ImmutableList`, never `List`.
- Inline validation errors (`irpfError: String?`) live inside `Loaded`, not as a separate state.
- No top-level `isSaving` when saving is scoped to a sub-item — put it on the item model.

## Intent

```kotlin
sealed interface EditHospitalIntent : Intent {
    data object Load : EditHospitalIntent
    data object NavigateBack : EditHospitalIntent
    data class UpdateIrpf(val value: String) : EditHospitalIntent
    data class ToggleExistingShiftSelection(val shiftId: UUID) : EditHospitalIntent
}
```

Parameter-less → `data object`; carries data → `data class`.
Navigation intents belong here; the ViewModel converts them into a SideEffect.

## SideEffect

```kotlin
sealed class EditHospitalSideEffect {
    data object NavigateBack : EditHospitalSideEffect()
}
```

Always `sealed class`. One-time events only (navigation, toasts) — never state that belongs in `Loaded`.

## Transform

```kotlin
internal object EditHospitalTransform {

    object ShowError : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState) = EditHospitalState.Error
    }

    data class UpdateIrpf(val irpf: String) : ViewTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(currentState: EditHospitalState): EditHospitalState {
            if (currentState !is EditHospitalState.Loaded) return currentState
            return currentState.copy(irpf = irpf, irpfError = validateIrpf(irpf))
        }
    }

    data class AddSideEffect(
        val sideEffect: EditHospitalSideEffect,
    ) : SideEffectTransform<EditHospitalState, EditHospitalSideEffect>() {
        override fun mutate(sideEffects: SideEffects<EditHospitalSideEffect>) = sideEffects.add(sideEffect)
    }
}
```

- Top-level `internal object [Feature]Transform`.
- Stateless → `object`; carries data → `data class`.
- Always guard with `if (currentState !is ...Loaded) return currentState`.
- Use `.toPersistentList()` when producing a new list from a mapped collection.
- The side-effect transform is always a `data class` named `AddSideEffect`.

## ViewModel

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

    override val state = reducer.state

    init { execute(EditHospitalIntent.Load) }

    override fun execute(intent: EditHospitalIntent) = reducer.executeIntent(intent)

    private fun executeIntent(intent: EditHospitalIntent) = when (intent) {
        is EditHospitalIntent.Load -> executeLoad(hospitalId)
        is EditHospitalIntent.NavigateBack -> executeAddSideEffect(EditHospitalSideEffect.NavigateBack)
        is EditHospitalIntent.UpdateIrpf -> executeUpdateIrpf(intent.value)
    }

    private fun executeSaveShift(): Flow<StateTransform<State<EditHospitalState, EditHospitalSideEffect>>> =
        flow {
            val loadedState = state.value.view as? EditHospitalState.Loaded ?: return@flow
            // … business logic …
            emit(EditHospitalTransform.SetFormSaving(false))
        }.onStart {
            emit(EditHospitalTransform.SetFormSaving(true))
        }
}
```

Rules:

- `@HiltViewModel` + single-line `@Inject constructor`.
- The initial `Load` fires from `init {}`, **never** from a `LaunchedEffect` in the Screen.
- Route args via `savedStateHandle.toRoute<RouteClass>()`; the route class must be `internal`.
- One private `execute[IntentName]()` per intent, each returning `Flow<StateTransform<...>>`.
- Use `.onStart { emit(...) }` to signal "saving started" before an async operation.
- Read current state synchronously with `state.value.view as? [Feature]State.Loaded`.
- Use `.catch { emit(Transform.ShowError) }` on observable flows.
- **No mutable class-level fields** (`MutableStateFlow`, `MutableSharedFlow`…). Only `reducer`,
  `state`, `init`, `execute`, private `execute*` funs, and `private val`s derived from constructor params.

## Mixed-content lazy lists

When a list mixes existing items with an inline form, model it as a sealed interface with a stable key:

```kotlin
@Immutable
sealed interface ShiftItem {
    val listKey: String

    @Immutable data class Existing(val model: ShiftUiModel, val isSelected: Boolean = false) : ShiftItem {
        override val listKey get() = model.id.toString()
    }

    @Immutable data class Form(
        val form: ShiftFormUiState = ShiftFormUiState(),
        val canSave: Boolean = false,
        val isSaving: Boolean = false,
        val isVisible: Boolean = false,
    ) : ShiftItem {
        override val listKey get() = "form"
    }
}
```

Use `key = { it.listKey }` in `items(...)`. The form item always exists; toggle it with
`isVisible` + `AnimatedVisibility`.

## DispatcherProvider

Defined in `:core:common`, constructor-injected into every ViewModel:

```kotlin
interface DispatcherProvider {
    fun main(): CoroutineDispatcher = Dispatchers.Main
    fun default(): CoroutineDispatcher = Dispatchers.Default
    fun io(): CoroutineDispatcher = Dispatchers.IO
    fun unconfined(): CoroutineDispatcher = Dispatchers.Unconfined
}

object DefaultDispatcherProvider : DispatcherProvider
```
