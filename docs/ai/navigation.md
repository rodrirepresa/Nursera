# Navigation conventions

Navigation Compose 2.8.3 with type-safe `@Serializable` routes.
Canonical example: `feature/hospital/presentation/.../list/navigation/HospitalNavigation.kt`.

## Structure

Each feature declares in `[Feature]Navigation.kt`:

```kotlin
@Serializable object HospitalGraph           // public — :app navigates here / tab selection
@Serializable object HospitalList            // public — :app needs it to know when to show the nav bar
@Serializable private object CreateHospital  // private — internal to the graph
@Serializable internal data class EditHospital(val hospitalId: String) // internal — ViewModel reads it

fun NavGraphBuilder.hospitalGraph(navController: NavController) {
    navigation<HospitalGraph>(startDestination = HospitalList) {
        composable<HospitalList> {
            HospitalScreen(
                navigateToCreateHospital = { navController.navigate(CreateHospital) },
                navigateToEditHospital = { id -> navController.navigate(EditHospital(id.toString())) },
            )
        }
        composable<CreateHospital> { CreateHospitalScreen(onNavigateBack = { navController.popBackStack() }) }
        composable<EditHospital> { EditHospitalScreen(onNavigateBack = { navController.popBackStack() }) }
    }
}
```

Visibility: routes `:app` needs → `public`; internal destinations → `private`;
routes a ViewModel reads via `SavedStateHandle` → `internal`.

## Rules

- Never pass a `NavController` into a `@Composable` screen — pass typed callback lambdas instead.
- Nav callbacks are wired only in `onSideEffect {}` inside the Screen.
- `:app` imports the graph extension function only — never `[Feature]Screen` or `[Feature]ViewModel`.

## Bottom navigation bar (`:app`)

- Material 3 `NavigationBar` + `NavigationBarItem` in the `Scaffold(bottomBar = …)` slot —
  never a hand-rolled `Row` of `clickable` icons, which loses the tab semantics and 48dp touch targets.
- Tab selection uses `NavDestination.hierarchy.hasRoute(KClass)` so the correct tab stays
  highlighted inside a nested graph.
- Tab clicks: `popUpTo(findStartDestination().id) { saveState = true }` + `launchSingleTop` +
  `restoreState = true` to preserve per-tab back stacks.
- Show/hide the bar with `AnimatedVisibility` **inside** the `bottomBar` slot, never with an `if`
  around it: `currentBackStackEntryAsState()` emits the new destination before the transition runs,
  so an `if` removes the bar instantly and makes the content jump.
- Each `TopLevelDestination` carries a `graphClass` (tab stays selected on detail screens) and a
  `tabRootClass` (bar is only visible on the tab's start destination).
