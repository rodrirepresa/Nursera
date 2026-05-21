package com.rodrirepresa.nursera.feature.schedule.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rodrirepresa.nursera.feature.schedule.presentation.ui.ScheduleScreen
import kotlinx.serialization.Serializable

@Serializable
object ScheduleRoute

fun NavGraphBuilder.scheduleScreen() {
    composable<ScheduleRoute> {
        ScheduleScreen()
    }
}
