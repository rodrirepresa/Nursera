package com.rodrirepresa.nursera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
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

@PreviewScreenSizes
@Composable
fun NurseraApp() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = currentBackStackEntry?.destination

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            TopLevelDestination.entries.forEach { destination ->
                val selected = currentDestination?.hierarchy?.any {
                    it.hasRoute(destination.routeClass)
                } == true
                item(
                    icon = { Icon(destination.icon, contentDescription = destination.label) },
                    label = { Text(destination.label) },
                    selected = selected,
                    onClick = {
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
    ) {
        NavHost(
            navController = navController,
            startDestination = HospitalGraph,
        ) {
            hospitalGraph(navController)
            favoritesScreen()
            profileScreen()
        }
    }
}
