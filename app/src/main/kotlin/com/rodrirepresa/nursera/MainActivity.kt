package com.rodrirepresa.nursera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rodrirepresa.nursera.feature.hospital.presentation.list.navigation.HospitalGraph
import com.rodrirepresa.nursera.feature.hospital.presentation.list.navigation.hospitalGraph
import com.rodrirepresa.nursera.feature.hospital.presentation.list.navigation.isHospitalTabRoot
import com.rodrirepresa.nursera.feature.profile.presentation.navigation.ProfileRoute
import com.rodrirepresa.nursera.feature.profile.presentation.navigation.profileScreen
import com.rodrirepresa.nursera.feature.schedule.presentation.navigation.ScheduleRoute
import com.rodrirepresa.nursera.feature.schedule.presentation.navigation.scheduleScreen
import com.rodrirepresa.nursera.ui.NurseraNavBar1
import com.rodrirepresa.nursera.ui.NurseraNavBar2
import com.rodrirepresa.nursera.ui.NurseraNavBar4
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

enum class TopLevelDestination(
    val label: String,
    val icon: ImageVector,
    val route: Any,
    val routeClass: KClass<*>,
) {
    HOSPITAL("Hospital", Icons.Default.Home, HospitalGraph, HospitalGraph::class),
    SCHEDULE("Schedule", Icons.Default.Favorite, ScheduleRoute, ScheduleRoute::class),
    PROFILE("Profile", Icons.Default.AccountBox, ProfileRoute, ProfileRoute::class),
}

@Composable
fun NurseraApp() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = currentBackStackEntry?.destination

    val isTopLevel = currentDestination.isHospitalTabRoot() ||
        currentDestination?.hasRoute(ScheduleRoute::class) == true ||
        currentDestination?.hasRoute(ProfileRoute::class) == true

    Scaffold(
        bottomBar = {
            if (isTopLevel) {
                NurseraNavBar4(
                    destinations = TopLevelDestination.entries,
                    isSelected = { destination ->
                        when (destination) {
                            TopLevelDestination.HOSPITAL -> currentDestination.isHospitalTabRoot()
                            else -> currentDestination?.hasRoute(destination.routeClass) == true
                        }
                    },
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
