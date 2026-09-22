package com.rodrirepresa.nursera.feature.hospital.presentation.list.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.rodrirepresa.nursera.feature.hospital.presentation.create.ui.CreateHospitalScreen
import com.rodrirepresa.nursera.feature.hospital.presentation.edit.ui.EditHospitalScreen
import com.rodrirepresa.nursera.feature.hospital.presentation.list.ui.HospitalScreen
import kotlinx.serialization.Serializable

@Serializable
object HospitalGraph

@Serializable
object HospitalList

@Serializable
private object CreateHospital

@Serializable
internal data class EditHospital(val hospitalId: String)

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
        composable<EditHospital> {
            EditHospitalScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
