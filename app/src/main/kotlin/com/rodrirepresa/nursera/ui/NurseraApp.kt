package com.rodrirepresa.nursera.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rodrirepresa.nursera.feature.hospital.presentation.list.navigation.HospitalGraph
import com.rodrirepresa.nursera.feature.hospital.presentation.list.navigation.hospitalGraph
import com.rodrirepresa.nursera.feature.earnings.presentation.navigation.earningsScreen
import com.rodrirepresa.nursera.feature.schedule.presentation.navigation.scheduleScreen
import com.rodrirepresa.nursera.navigation.TopLevelDestination
import com.rodrirepresa.nursera.navigation.isSelected
import com.rodrirepresa.nursera.navigation.isTopLevel

@Composable
fun NurseraApp() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = currentBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            // Keeping the bar in the composition and animating it lets Scaffold keep measuring it
            // during the transition, so the content does not jump when the bar hides.
            AnimatedVisibility(
                visible = currentDestination.isTopLevel(),
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
            earningsScreen()
        }
    }
}
