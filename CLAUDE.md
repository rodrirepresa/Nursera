# Nursera — AI Guide

Android app for nurses in Spain who work across multiple hospitals. They configure hospitals with
pay rates per shift type, log shifts in a monthly calendar, and get notified when projected net
income crosses their personal target.

> **This file is injected into every request. Keep it short.**
> Detailed conventions live in `docs/ai/` — read only the file the current task needs.

## Documentation index

| Read | When you are... |
|---|---|
| `docs/ai/mvi.md` | Writing a ViewModel, State, Intent, SideEffect or Transform |
| `docs/ai/compose.md` | Writing a Screen, a `core/ui` component, UI models or validators |
| `docs/ai/navigation.md` | Adding a route, a nested graph, or touching the bottom nav bar |
| `docs/ai/gradle.md` | Creating a module or editing any `build.gradle.kts` |
| `docs/ai/testing.md` | Writing or fixing unit tests |

Canonical implementations — read the real file instead of asking for an example:

- ViewModel + Transform → `feature/hospital/presentation/src/main/kotlin/com/rodrirepresa/nursera/feature/hospital/presentation/edit/viewmodel/`
- Screen → `.../edit/ui/EditHospitalScreen.kt`
- Nav graph → `.../list/navigation/HospitalNavigation.kt`
- Unit test → `feature/hospital/presentation/src/test/.../EditHospitalViewModelTest.kt`
- Design system → `core/ui/src/main/kotlin/com/rodrirepresa/nursera/core/ui/`

## Modules

```text
app/                         DI graph root, MainActivity, bottom nav
core/common/                 DispatcherProvider
core/ui/                     Design system (Nursera* composables)
feature/<name>/data          nursera.kotlin.library
feature/<name>/domain        nursera.kotlin.library
feature/<name>/presentation  nursera.android.library.compose + nursera.android.library.hilt
```

Existing features: `hospital`, `schedule`, `profile`.
New features always get their own `feature/<name>/` module — never put feature logic in `app/` or `core/`.

## Stack

Kotlin 2.0.21 · Compose BOM 2024.09.00 · Adidas MVI 1.9.5 + mvi-compose 0.3.0 · Hilt 2.52 (KSP) ·
Coroutines/Flow 1.8.1 · Navigation Compose 2.8.3 (type-safe `@Serializable` routes) ·
kotlinx.serialization 1.7.3 · kotlinx.collections.immutable · ktlint · JUnit + Turbine.

Networking and local DB are **not chosen yet**. Versions live in `gradle/libs.versions.toml`.

## Commands

```bash
./gradlew ktlintFormat                                       # fix formatting
./gradlew ktlintCheck                                        # CI check
./gradlew :feature:hospital:presentation:testDebugUnitTest   # targeted tests (prefer this)
./gradlew testDebugUnitTest                                  # all tests
./gradlew assembleDebug
```

## Hard rules

**Architecture**

- Layers: presentation → domain → data. Never skip a layer.
- Repository interface in `:domain`, `[Feature]RepositoryImpl` in `:data`.
- One use case per class, verb + noun: `AddShiftToHospitalUseCase` + `...Impl`.
- Every new screen gets the full MVI set: State, Intent, SideEffect, Transform, ViewModel.
- Hilt: `@Singleton` for repositories and data sources, `@ViewModelScoped` for stateful use cases.
  Never inject `Context` directly — use `@ApplicationContext`.

**Boundaries**

- Domain models are importable only by ViewModels and mappers — never by Composables, State or Transform.
- `[Feature]Screen` and `[Feature]ViewModel` are `internal`; `:app` only uses `NavGraphBuilder.[feature]Graph()`.
- No business logic in Composables.

**Kotlin**

- `sealed interface` for State and Intent, `sealed class` for SideEffect.
- `ImmutableList` + `persistentListOf()` in State — never `List`.
- `@Immutable` on every UI model and sealed UI item interface.
- Prefer `data class` for models. Avoid nullable types; prefer empty-string defaults.
- Coroutines are launched from the ViewModel via `viewModelScope`.
- No `LiveData`, no `GlobalScope`, no RxJava, no XML views.
- Never use the word `Cache` in Intent or Transform names — use domain verbs (`AddMonth`, `SetDisplayedMonth`).

**Process**

- Ask before adding a dependency; check `gradle/libs.versions.toml` first.
- Write unit tests alongside any new ViewModel or use case.
- When a convention or library changes, update this file **and** the matching `docs/ai/` file.
