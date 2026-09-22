---
description: Create a new use case in a Nursera feature — domain interface, implementation, repository method if needed, Hilt binding in the data module, and wiring into the ViewModel.
---

Create a use case called **$ARGUMENTS**.

Expected input: `<feature> <UseCaseName>` — e.g. `schedule ObserveWeekSchedule`.
The name must be **verb + noun** and must not already end in `UseCase` (this skill appends it).
If the user gave a noun-first name, propose a verb-first alternative before proceeding.

Below, `[f]` = feature lowercase, `[F]` = feature capitalised, `[U]` = use case name.

---

## 1. Check it does not already exist

List `feature/[f]/domain/src/main/kotlin/.../domain/usecase/`. If something equivalent exists,
stop and tell the user instead of creating a duplicate.

## 2. Repository contract

If the use case needs data the repository does not expose yet:

- Add the method to `feature/[f]/domain/repository/[F]Repository.kt`.
- Implement it in `feature/[f]/data/repository/[F]RepositoryImpl.kt`.
- Observation methods return `Flow<T>`; one-shot writes are `suspend fun`.

## 3. Interface — `domain/usecase/[U]UseCase.kt`

```kotlin
package com.rodrirepresa.nursera.feature.[f].domain.usecase

interface [U]UseCase {
    operator fun invoke(id: UUID): Flow<Something>       // observation
    // or: suspend operator fun invoke(param: Something) // one-shot
}
```

Always `operator fun invoke` so call sites read as `observeWeekScheduleUseCase(id)`.

## 4. Implementation — `domain/usecase/[U]UseCaseImpl.kt`

```kotlin
class [U]UseCaseImpl
    @Inject
    constructor(
        private val repository: [F]Repository,
    ) : [U]UseCase {
        override fun invoke(id: UUID): Flow<Something> = repository.something(id)
    }
```

Keep the indented `@Inject constructor` layout — that is what ktlint produces in this repo.
Business rules (filtering, aggregation, net-income maths) belong here, not in the repository
and not in the ViewModel.

## 5. Hilt binding — `feature/[f]/data/di/[F]DataModule.kt`

Add to the existing `abstract class [F]DataModule`:

```kotlin
@Binds
abstract fun bind[U]UseCase(impl: [U]UseCaseImpl): [U]UseCase
```

The module is `@InstallIn(SingletonComponent::class)`. Do not create a second module for the feature.

## 6. Wire it into the ViewModel

Only if the user asked for it. Add the use case to the `@Inject constructor` as a `private val`,
call it from the relevant private `execute*` fun, map domain → UI models before emitting a
Transform, and add `.catch { emit([F]Transform.ShowError) }` on observed flows.
Never import domain models into a Composable, State or Transform.

## 7. Test

Add a unit test for the use case if it contains real logic (anything beyond a one-line delegation
to the repository), using a fake repository. If it only delegates, skip it and say so.
If a ViewModel changed, update its existing test.

## 8. Verify

```bash
./gradlew ktlintFormat
./gradlew :feature:[f]:presentation:testDebugUnitTest
```

## 9. Report back

List created and modified files, and flag whether the repository contract changed.
