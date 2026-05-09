package com.rodrirepresa.nursera.feature.hospital.presentation.list.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.rodrirepresa.nursera.feature.hospital.presentation.create.ui.CreateHospitalScreen
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.ui.EditHospitalScreen
import com.rodrirepresa.nursera.feature.hospital.presentation.list.ui.HospitalScreen
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
object HospitalGraph

@Serializable
private object HospitalList

@Serializable
private object CreateHospital

@Serializable
private data class EditHospital(val hospitalId: String)

fun NavDestination?.isHospitalTabRoot(): Boolean = this?.hierarchy?.any { it.hasRoute(HospitalList::class) } == true

fun NavGraphBuilder.hospitalGraph(navController: NavController) {
    navigation<HospitalGraph>(startDestination = HospitalList) {
        composable<HospitalList> {
            HospitalScreen(
                navigateToCreateHospital = { navController.navigate(CreateHospital) },
                navigateToEditHospital = { id -> navController.navigate(EditHospital(id.toString())) },
            )
        }
        composable<CreateHospital> {
            CreateHospitalScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable<EditHospital> { backStackEntry ->
            val route = backStackEntry.toRoute<EditHospital>()
            EditHospitalScreen(
                hospitalId = UUID.fromString(route.hospitalId),
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
