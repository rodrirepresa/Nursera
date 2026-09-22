package com.rodrirepresa.nursera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rodrirepresa.nursera.feature.hospital.presentation.list.navigation.HospitalGraph
import com.rodrirepresa.nursera.feature.hospital.presentation.list.navigation.HospitalList
import com.rodrirepresa.nursera.feature.hospital.presentation.list.navigation.hospitalGraph
import com.rodrirepresa.nursera.feature.profile.presentation.navigation.ProfileRoute
import com.rodrirepresa.nursera.feature.profile.presentation.navigation.profileScreen
import com.rodrirepresa.nursera.feature.schedule.presentation.navigation.ScheduleRoute
import com.rodrirepresa.nursera.feature.schedule.presentation.navigation.scheduleScreen
import com.rodrirepresa.nursera.ui.NurseraNavigationBar
import com.rodrirepresa.nursera.ui.theme.NurseraTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.reflect.KClass

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NurseraTheme {
                NurseraApp()
            }
        }
    }
}

/**
 * @param route the destination handed to [androidx.navigation.NavController.navigate] on tab click.
 * @param graphClass identifies the whole tab, including its detail screens, so the tab stays
 * highlighted while the user drills down.
 * @param tabRootClass identifies only the tab's start destination, which is where the navigation
 * bar is meant to be visible.
 */
enum class TopLevelDestination(
    @StringRes val labelRes: Int,
    val icon: ImageVector,
    val route: Any,
    val graphClass: KClass<*>,
    val tabRootClass: KClass<*>,
) {
    HOSPITAL(R.string.nav_hospitals, Icons.Default.Home, HospitalGraph, HospitalGraph::class, HospitalList::class),
    SCHEDULE(R.string.nav_schedule, Icons.Default.DateRange, ScheduleRoute, ScheduleRoute::class, ScheduleRoute::class),
    PROFILE(R.string.nav_profile, Icons.Default.Person, ProfileRoute, ProfileRoute::class, ProfileRoute::class),
}

private fun NavDestination?.isSelected(destination: TopLevelDestination): Boolean =
    this?.hierarchy?.any { it.hasRoute(destination.graphClass) } == true

private fun NavDestination?.isTopLevel(): Boolean =
    TopLevelDestination.entries.any { this?.hasRoute(it.tabRootClass) == true }

@Composable
fun NurseraApp() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = currentBackStackEntry?.destination
    val isTopLevel = currentDestination.isTopLevel()

    Scaffold(
        bottomBar = {
            // Keeping the bar in the composition and animating it lets Scaffold keep measuring it
            // during the transition, so the content does not jump when the bar hides.
            AnimatedVisibility(
                visible = isTopLevel,
                enter = slideInVertically { height -> height },
                exit = slideOutVertically { height -> height },
            ) {
                NurseraNavigationBar(
                    destinations = TopLevelDestination.entries,
                    isSelected = { destination -> currentDestination.isSelected(destination) },
                    onDestinationClick = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HospitalGraph,
            modifier = Modifier.padding(innerPadding),
        ) {
            hospitalGraph(navController)
            scheduleScreen()
            profileScreen()
        }
    }
}
