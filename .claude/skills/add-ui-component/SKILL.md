---
description: Create a new shared Compose component in Nursera's core/ui design system, with the mandatory previews for every visual state.
---

Create a design-system component called **$ARGUMENTS** in `:core:ui`.

The name must start with the `Nursera` prefix. If the user gave a bare name (`Badge`), use
`NurseraBadge` and say so. Historical `NeoBrutalist*` names exist but are legacy — do not add more.

File: `core/ui/src/main/kotlin/com/rodrirepresa/nursera/core/ui/[Name].kt`
(one component per file, filename matches the composable name).

**Read `docs/ai/compose.md` before writing code.**

---

## 1. Check it does not already exist

Existing components: `NurseraCard`, `NurseraChip`, `NurseraCta`, `NurseraHeader`,
`NurseraIconButton`, `NurseraTextField`, `NurseraToolbar`, `NurseraStateViews`
(`NurseraLoadingView` / `NurseraErrorView`), `NeoBrutalistDayCard`, plus `NurseraPalette`,
`ColorExtension` (`Color.darken()`) and `LocaleProvider`.

If the request overlaps one of these, propose extending it with a new parameter instead of
creating a near-duplicate. **Only create a new file if it is genuinely a new component.**

## 2. Signature rules

```kotlin
@Composable
fun NurseraBadge(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    content: @Composable () -> Unit,
) { ... }
```

- `public` — this is a shared module.
- Always expose a `modifier: Modifier = Modifier` parameter and apply it to the outermost element.
- **Stateless only**: no `remember` holding business state, no ViewModel, no use cases, no
  domain-model imports. State is hoisted to the caller.
- Prefer a `content: @Composable () -> Unit` slot over a `text: String` parameter when the caller
  may need custom styling.
- Take colours as `Color` parameters or read them from `NurseraPalette` / `MaterialTheme` —
  never hardcode a hex literal in the component body.
- Reuse `Color.darken()` from `ColorExtension.kt` for the neo-brutalist offset shadow instead of
  computing a shade inline.
- No string literals inside the component; the caller passes text.

## 3. Visual style

Follow the neo-brutalist pattern already used in `NurseraChip.kt`: an offset shadow `Box`
(`.matchParentSize().offset(x = 2.dp, y = 2.dp)`) behind the foreground `Box`, both with the same
`border` + `RoundedCornerShape`. Read `NurseraChip.kt` and `NurseraCard.kt` before styling.

## 4. Previews — mandatory

Every `public` composable in `core/ui` must have at least one `@Preview`, and **each distinct
visual state gets its own `private` preview function** in the same file:

```kotlin
@Preview(showBackground = true)
@Composable
private fun NurseraBadgePreview() { ... }

@Preview(showBackground = true)
@Composable
private fun NurseraBadgeAccentPreview() { ... }
```

Cover the relevant states: default, accent/alternative colour, pressed, disabled, error,
loading, long-text overflow.

## 5. Verify

```bash
./gradlew ktlintFormat
./gradlew :core:ui:assembleDebug
```

Then confirm the previews render in Android Studio's Split view.

## 6. Report back

Name the file created, list the preview functions, and if the component supersedes an existing
one, say which call sites should migrate.
