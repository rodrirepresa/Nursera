package com.rodrirepresa.nursera.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy

/**
 * Whether [destination] is the tab the user is currently in.
 *
 * Matching against the whole hierarchy keeps the tab highlighted while the user is on one of its
 * detail screens.
 */
internal fun NavDestination?.isSelected(destination: TopLevelDestination): Boolean =
    this?.hierarchy?.any { it.hasRoute(destination.graphClass) } == true

/**
 * Whether the current destination is a tab root, which is where the navigation bar is shown.
 *
 * Unlike [isSelected] this matches the exact destination, so drilling into a detail screen hides
 * the bar.
 */
internal fun NavDestination?.isTopLevel(): Boolean =
    TopLevelDestination.entries.any { this?.hasRoute(it.tabRootClass) == true }
