package com.rodrirepresa.nursera.feature.hospital.presentation.list.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.rodrirepresa.nursera.feature.hospital.presentation.create.ui.CreateHospitalScreen
import com.rodrirepresa.nursera.feature.hospital.presentation.list.ui.HospitalScreen
import kotlinx.serialization.Serializable

@Serializable
object HospitalGraph

@Serializable
private object HospitalList

@Serializable
private object CreateHospital

fun NavGraphBuilder.hospitalGraph(navController: NavController) {
    navigation<HospitalGraph>(startDestination = HospitalList) {
        composable<HospitalList> {
            HospitalScreen(
                navigateToCreateHospital = { navController.navigate(CreateHospital) },
            )
        }
        composable<CreateHospital> {
            CreateHospitalScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
