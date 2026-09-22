package com.rodrirepresa.nursera.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.rodrirepresa.nursera.R
import com.rodrirepresa.nursera.feature.hospital.presentation.list.navigation.HospitalGraph
import com.rodrirepresa.nursera.feature.hospital.presentation.list.navigation.HospitalList
import com.rodrirepresa.nursera.feature.earnings.presentation.navigation.EarningsRoute
import com.rodrirepresa.nursera.feature.schedule.presentation.navigation.ScheduleRoute
import kotlin.reflect.KClass

/**
 * The tabs shown in the bottom navigation bar.
 *
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
    EARNINGS(R.string.nav_earnings, Icons.Default.Person, EarningsRoute, EarningsRoute::class, EarningsRoute::class),
}
