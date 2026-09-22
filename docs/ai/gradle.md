# Gradle conventions

## Convention plugins

All modules use plugins from `build-logic/convention`. Never repeat `compileSdk`, `minSdk`,
`compileOptions` or `jvmTarget` in a module's `build.gradle.kts`.

| Plugin | Use when |
|---|---|
| `nursera.kotlin.library` | Pure Kotlin — no Android APIs (domain, data, core utilities) |
| `nursera.android.library` | Needs Android APIs but no Compose |
| `nursera.android.library.compose` | Needs Compose |
| `nursera.android.library.hilt` | Needs Hilt + KSP |
| `nursera.android.application` | The `:app` module only |

**Default to `nursera.kotlin.library`.** Only use an Android plugin if the module imports Android APIs.
A pure Kotlin module has no `AndroidManifest.xml` and no `android {}` block.

## `dependencies {}` sections

Always in this order, omitting empty sections:

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

- **Projects** — internal modules (`projects.*`). Always type-safe accessors, never `project(":…")`.
- **Libraries** — external dependencies (`libs.*`) declared in `gradle/libs.versions.toml`.
- **Debug** — `debugImplementation`.
- **Test** — `testImplementation` / `androidTestImplementation`.

Ask before adding a new dependency; check `gradle/libs.versions.toml` first in case something
already covers the need.

## Registering a module

Add to `settings.gradle.kts` next to the existing `include` entries:

```kotlin
include(":feature:<name>:data")
include(":feature:<name>:domain")
include(":feature:<name>:presentation")
```

Type-safe project accessors are enabled via `enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")`.

## CI

`.github/workflows/ci.yml` runs on every push/PR to `main`, on JDK 21:

1. `./gradlew ktlintCheck`
2. `./gradlew testDebugUnitTest`
3. `./gradlew assembleDebug`

Run `./gradlew ktlintFormat` before committing.
