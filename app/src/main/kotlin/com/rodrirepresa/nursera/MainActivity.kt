package com.rodrirepresa.nursera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rodrirepresa.nursera.feature.favorites.presentation.navigation.FavoritesRoute
import com.rodrirepresa.nursera.feature.favorites.presentation.navigation.favoritesScreen
import com.rodrirepresa.nursera.feature.hospital.presentation.list.navigation.HospitalGraph
import com.rodrirepresa.nursera.feature.hospital.presentation.list.navigation.hospitalGraph
import com.rodrirepresa.nursera.feature.profile.presentation.navigation.ProfileRoute
import com.rodrirepresa.nursera.feature.profile.presentation.navigation.profileScreen
import com.rodrirepresa.nursera.ui.NurseraNavBar
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
    FAVORITES("Favorites", Icons.Default.Favorite, FavoritesRoute, FavoritesRoute::class),
    PROFILE("Profile", Icons.Default.AccountBox, ProfileRoute, ProfileRoute::class),
}

@Composable
fun NurseraApp() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = currentBackStackEntry?.destination

    // Full-screen box — content fills edge-to-edge, nav bar overlays at the bottom
    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = HospitalGraph,
            modifier = Modifier.fillMaxSize(),
        ) {
            hospitalGraph(navController)
            favoritesScreen()
            profileScreen()
        }

        NurseraNavBar(
            destinations = TopLevelDestination.entries,
            isSelected = { destination ->
                currentDestination?.hierarchy?.any {
                    it.hasRoute(destination.routeClass)
                } == true
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
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
