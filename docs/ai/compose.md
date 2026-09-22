# Compose conventions

## Screen

```kotlin
@Composable
internal fun EditHospitalScreen(
    onNavigateBack: () -> Unit,
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
            is EditHospitalState.Error -> NurseraErrorView(stringResource(R.string.edit_hospital_error_message))
            is EditHospitalState.Loaded -> EditHospitalLoadedContent(state, viewModel::execute)
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

- One screen = one `internal` `[Feature]Screen` composable; `[Feature]LoadedContent` is always `private`.
- The Screen receives navigation lambdas (`onNavigateBack`, `navigateTo*`) and wires them **only**
  inside `onSideEffect {}`. Child composables never receive nav lambdas — they fire intents.
- Child composables receive a single `executeIntent: ([Feature]Intent) -> Unit`, wired as
  `executeIntent = viewModel::execute`.
- Use `NurseraLoadingView` / `NurseraErrorView` from `core/ui` for loading and error states.
- No business logic in Composables.
- `isSaving` for a sub-item operation lives on the item model (`ShiftItem.Form.isSaving`),
  surfaced via `NurseraCta(isSaving = …)`.
- Provide a `@Preview` for every screen state: Loading, Error, Loaded, Loaded+selection, Loaded+saving…

## UI models

| Kind | Location | Example |
|---|---|---|
| Display (read-only) | `<screen>/viewmodel/` | `ShiftUiModel.kt` |
| Form state (editable) | `<screen>/model/` | `ShiftFormUiState.kt` |

- Both are `@Immutable data class` with pre-formatted strings — no `LocalTime`, `UUID` or domain types.
- Mappers: `internal` extension functions in `<screen>/mappers/`, named `DomainModel.toUiModel()`.
- Mapping happens in the ViewModel inside the Flow before emitting a Transform — never in a Composable.
- Domain imports are allowed only in the ViewModel (use cases) and mapper files.

## Validators

Pure Kotlin extension functions in `<screen>/validators/`, one file per concern
(`IrpfValidator.kt`, `ShiftFormUiStateValidator.kt`, `TimeValidator.kt`):

```kotlin
internal fun validateIrpf(value: String): String?          // null = valid
internal fun ShiftFormUiState.withValidation(): ShiftFormUiState
internal fun ShiftFormUiState.isValid(): Boolean
```

Always `internal`. Called from the ViewModel or a Transform, never from a Composable.

## Strings

Every user-facing string lives in the feature module's `res/values/strings.xml` **and**
`res/values-es/strings.xml`. Key prefix: `<feature>_<screen>_`.

## `core/ui` design system

Components: `NurseraCard`, `NurseraChip`, `NurseraCta`, `NurseraHeader`, `NurseraIconButton`,
`NurseraTextField`, `NurseraToolbar`, `NurseraStateViews` (`NurseraLoadingView` / `NurseraErrorView`),
`NeoBrutalistDayCard`. Helpers: `NurseraPalette`, `ColorExtension` (`Color.darken()`), `LocaleProvider`.

- Every `public` `@Composable` in `core/ui` **must** have at least one `@Preview`.
- Each distinct visual state (default, pressed, error, disabled) gets its own `@Preview` function.
- Preview functions are `private` and live in the same file as the component.
- Components are stateless: no ViewModel, no use cases, no domain-model imports, no string literals.
- Always expose `modifier: Modifier = Modifier` and apply it to the outermost element.
- Neo-brutalist styling uses an offset shadow `Box` behind the foreground `Box`; reuse
  `Color.darken()` instead of computing shades inline. See `NurseraChip.kt`.
