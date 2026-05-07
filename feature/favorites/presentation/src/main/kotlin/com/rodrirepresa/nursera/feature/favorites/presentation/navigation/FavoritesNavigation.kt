package com.rodrirepresa.nursera.feature.favorites.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rodrirepresa.nursera.feature.favorites.presentation.ui.FavoritesScreen
import kotlinx.serialization.Serializable

@Serializable
object FavoritesRoute

fun NavGraphBuilder.favoritesScreen() {
    composable<FavoritesRoute> {
        FavoritesScreen()
    }
}
