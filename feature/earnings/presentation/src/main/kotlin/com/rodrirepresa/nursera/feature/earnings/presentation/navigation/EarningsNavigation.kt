package com.rodrirepresa.nursera.feature.earnings.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rodrirepresa.nursera.feature.earnings.presentation.ui.EarningsScreen
import kotlinx.serialization.Serializable

@Serializable
object EarningsRoute

fun NavGraphBuilder.earningsScreen() {
    composable<EarningsRoute> {
        EarningsScreen()
    }
}
