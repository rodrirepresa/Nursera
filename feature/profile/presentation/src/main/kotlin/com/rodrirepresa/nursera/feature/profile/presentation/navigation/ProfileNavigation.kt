package com.rodrirepresa.nursera.feature.profile.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rodrirepresa.nursera.feature.profile.presentation.ui.ProfileScreen
import kotlinx.serialization.Serializable

@Serializable
object ProfileRoute

fun NavGraphBuilder.profileScreen() {
    composable<ProfileRoute> {
        ProfileScreen()
    }
}
