---
description: Scaffold a new feature module for the Nursera project. Creates data, domain and presentation modules with full MVI boilerplate and a unit test, following CLAUDE.md and docs/ai/.
---

Scaffold a new feature called **$ARGUMENTS** (lowercase, e.g. `tracking`).
Derive the class prefix by capitalising it (`tracking` → `Tracking`), referred to below as `[F]`.
Package root: `com.rodrirepresa.nursera.feature.$ARGUMENTS`.

**Before writing any code, read `docs/ai/mvi.md`, `docs/ai/compose.md`, `docs/ai/navigation.md`
and `docs/ai/gradle.md`.** They are the source of truth — this skill only lists the steps.

Copy the structure of the `profile` feature, which is the smallest complete reference.

---

## 1. Register the modules

Add to `settings.gradle.kts`:

```kotlin
include(":feature:$ARGUMENTS:data")
include(":feature:$ARGUMENTS:domain")
include(":feature:$ARGUMENTS:presentation")
```

## 2. Build files

- `feature/$ARGUMENTS/domain/build.gradle.kts` → `nursera.kotlin.library`, no dependencies.
- `feature/$ARGUMENTS/data/build.gradle.kts` → `nursera.kotlin.library`, depends on the domain module.
- `feature/$ARGUMENTS/presentation/build.gradle.kts` → copy
  `feature/profile/presentation/build.gradle.kts` verbatim and swap the namespace and project
  accessors. Do not invent dependency coordinates — reuse the ones already there.

Follow the section order in `docs/ai/gradle.md`: Projects → Libraries → Debug → Test.

## 3. Domain

- `domain/repository/[F]Repository.kt` — interface.
- `domain/model/` — domain entities as needed.
- `domain/usecase/` — one interface + one `...Impl` per use case, named verb + noun.

## 4. Data

- `data/repository/[F]RepositoryImpl.kt` — implements the domain interface.
- `data/di/[F]DataModule.kt` — `@Module @InstallIn(SingletonComponent::class) abstract class`
  with `@Binds` for the repository and every use case. See `HospitalDataModule.kt`.

## 5. Presentation

Create, under `presentation/src/main/kotlin/.../presentation/`:

- `viewmodel/[F]State.kt` — `sealed interface [F]State : LoggableState` with `Loading`, `Error`
  (`data object`) and `Loaded` (`data class`, `ImmutableList` fields only).
- `viewmodel/[F]Intent.kt` — `sealed interface [F]Intent : Intent`, with at least `Load`.
- `viewmodel/[F]SideEffect.kt` — `sealed class [F]SideEffect`.
- `viewmodel/[F]Transform.kt` — `internal object [F]Transform` with `ShowError`, the state
  transforms, and `data class AddSideEffect`.
- `viewmodel/[F]ViewModel.kt` — `@HiltViewModel internal class`, fires `Load` from `init {}`
  (**never** from a `LaunchedEffect`), one private `execute*` fun per intent, no mutable fields.
- `ui/[F]Screen.kt` — `internal` composable using `MviContainer`, `NurseraLoadingView` /
  `NurseraErrorView`, plus a `private [F]LoadedContent` and a `@Preview` per state.
- `navigation/[F]Navigation.kt` — `@Serializable object [F]Graph`, `@Serializable object [F]Home`,
  and `fun NavGraphBuilder.${ARGUMENTS}Graph(navController: NavController)` wrapping
  `navigation<[F]Graph>(startDestination = [F]Home)`.
  **Do not generate a `[F]Route()` composable** — the graph extension is the only public entry point.
- `mappers/`, `validators/`, `model/` only if the feature needs them.

Add user-facing strings to `res/values/strings.xml` and `res/values-es/strings.xml`.

## 6. Unit test

`presentation/src/test/kotlin/.../viewmodel/[F]ViewModelTest.kt`, following `docs/ai/testing.md`:
fake `DispatcherProvider` over `StandardTestDispatcher`, Turbine, and tests for both the ViewModel
transitions and the pure `[F]Transform` mutations.

## 7. Verify

```bash
./gradlew ktlintFormat
./gradlew :feature:$ARGUMENTS:presentation:testDebugUnitTest
./gradlew assembleDebug
```

## 8. Report back

Tell the user:

- Which files were created.
- To sync Gradle.
- That a nav-bar entry must be added manually to `TopLevelDestination` in `:app`, plus the call to
  `${ARGUMENTS}Graph(navController)` in `NurseraApp.kt`.
- Whether `CLAUDE.md` or any `docs/ai/` file needs updating.
