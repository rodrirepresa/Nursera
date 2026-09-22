---
description: Add a new screen to an existing Nursera feature module. Creates the full MVI set (State, Intent, SideEffect, Transform, ViewModel), the Screen composable with previews, a route in the feature graph, and a unit test.
---

Add a screen called **$ARGUMENTS** to an existing feature.

Expected input format: `<feature> <ScreenName>` — e.g. `hospital ArchiveHospital`.
If the user only gave a screen name, ask which feature module it belongs to before starting.

Below, `[f]` = feature name lowercase, `[S]` = screen name capitalised.
Package root: `com.rodrirepresa.nursera.feature.[f].presentation`.

**Read `docs/ai/mvi.md` and `docs/ai/compose.md` before writing code.** They are the source of truth.
Canonical reference: the `edit` screen of the `hospital` feature.

---

## 1. Decide the package layout

If the feature already has per-screen subpackages (like `hospital` → `create/`, `edit/`, `list/`),
create a new `[screenName]/` subpackage with `ui/` and `viewmodel/`.

If the feature is flat (like `profile` → `ui/`, `viewmodel/`, `navigation/`), **first migrate it**
to per-screen subpackages, then add the new one. Tell the user you are doing this and why.

Add `model/`, `mappers/` or `validators/` subpackages only if this screen actually needs them.

## 2. MVI set — `[screenPkg]/viewmodel/`

- `[S]State.kt` — `sealed interface [S]State : LoggableState` with `data object Loading`,
  `data object Error`, and `data class Loaded`. Lists are `ImmutableList` + `persistentListOf()`.
  Inline validation errors are nullable String fields inside `Loaded`.
- `[S]Intent.kt` — `sealed interface [S]Intent : Intent`. Include `Load`, the user actions, and a
  navigation intent per destination (e.g. `NavigateBack`).
- `[S]SideEffect.kt` — `sealed class [S]SideEffect` with one `data object` per one-off event.
- `[S]Transform.kt` — `internal object [S]Transform`. Stateless → `object`, carries data →
  `data class`. Every state transform guards with
  `if (currentState !is [S]State.Loaded) return currentState`.
  Always include `data class AddSideEffect(...) : SideEffectTransform<...>`.
- `[S]ViewModel.kt` — `@HiltViewModel internal class`, fires `Load` from `init {}`, one private
  `execute*` fun per intent, no mutable class-level fields.
- Display UI models (`[S]UiModel.kt`) also live here, as `@Immutable data class` with pre-formatted
  strings — no `UUID`, `LocalTime` or domain types.

If the screen takes route arguments, inject `SavedStateHandle` and read them with
`savedStateHandle.toRoute<[S]>()`. The route class must then be declared `internal`.

## 3. Screen — `[screenPkg]/ui/[S]Screen.kt`

- `internal fun [S]Screen(onNavigateBack: () -> Unit, viewModel: [S]ViewModel = hiltViewModel())`.
- Body is `MviContainer`; wire every nav lambda inside `onSideEffect {}` and nowhere else.
- `when (state)` → `NurseraLoadingView()`, `NurseraErrorView(...)`, `[S]LoadedContent(...)`.
- `private fun [S]LoadedContent(state, executeIntent, modifier = Modifier)`.
- Child composables get only `executeIntent: ([S]Intent) -> Unit` — never nav lambdas.
- Reuse `core/ui` components (`NurseraCard`, `NurseraCta`, `NurseraTextField`, `NurseraToolbar`,
  `NurseraChip`, `NurseraHeader`). Do not hand-roll styling that already exists there.
- Add a `private @Preview` per state: Loading, Error, Loaded, plus meaningful variants
  (empty, selection active, saving).

## 4. Strings

Add every user-facing string to the feature module's `res/values/strings.xml` **and**
`res/values-es/strings.xml`. Key prefix: `[f]_[screen]_`.

## 5. Route — `[f]/.../navigation/[F]Navigation.kt`

Add to the existing graph file:

```kotlin
@Serializable private object [S]                          // no args
@Serializable internal data class [S](val someId: String) // args read by the ViewModel
```

and inside `navigation<[F]Graph>(...)`:

```kotlin
composable<[S]> {
    [S]Screen(onNavigateBack = { navController.popBackStack() })
}
```

Then wire the caller: the screen that navigates here gets a `navigateTo[S]` lambda fired from its
own `onSideEffect {}`. Never pass a `NavController` into a composable.

## 6. Test — `presentation/src/test/.../viewmodel/[S]ViewModelTest.kt`

Follow `docs/ai/testing.md`: fake `DispatcherProvider` over `StandardTestDispatcher`, Turbine,
`advanceUntilIdle()`. Cover the initial `Load`, each intent, and the pure `[S]Transform` mutations.
For route args, pass `SavedStateHandle(mapOf("someId" to value))` to the constructor.

## 7. Verify

```bash
./gradlew ktlintFormat
./gradlew :feature:[f]:presentation:testDebugUnitTest
./gradlew assembleDebug
```

## 8. Report back

List the created files, the new route, which screen now navigates to it, and whether
`CLAUDE.md` or a `docs/ai/` file needs updating.
