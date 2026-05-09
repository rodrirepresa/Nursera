package com.rodrirepresa.nursera.feature.hospital.presentation.list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adidas.mvi.compose.MviContainer
import com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel.HospitalIntent
import com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel.HospitalSideEffect
import com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel.HospitalState
import com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel.HospitalViewModel
import kotlinx.collections.immutable.ImmutableList
import java.util.UUID

@Composable
internal fun HospitalScreen(
    navigateToCreateHospital: () -> Unit,
    navigateToEditHospital: (UUID) -> Unit,
    viewModel: HospitalViewModel = hiltViewModel(),
) {
    MviContainer(
        state = viewModel.state,
        onSideEffect = { sideEffect ->
            consumeSideEffects(
                sideEffect = sideEffect,
                navigateToCreateHospital = navigateToCreateHospital,
                navigateToEditHospital = navigateToEditHospital,
            )
        },
    ) { state ->
        Scaffold { innerPadding ->
            when (state) {
                is HospitalState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is HospitalState.Loaded -> {
                    HospitalScreenLoaded(
                        innerPadding = innerPadding,
                        hospitalList = state.hospitals,
                        executeIntent = viewModel::execute,
                    )
                }
            }
        }
    }
}

@Composable
private fun HospitalScreenLoaded(
    innerPadding: PaddingValues,
    hospitalList: ImmutableList<HospitalUiModel>,
    executeIntent: (HospitalIntent) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .padding(innerPadding)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                ),
    ) {
        val label =
            when (val size = hospitalList.size) {
                0 -> "Añade aquí tus centros de trabajo"
                1 -> "Un centro de trabajo"
                else -> "$size centros de trabajo"
            }

        Header(
            modifier = Modifier.padding(bottom = 8.dp),
            title = "Mis Hospitales",
            subtitle = label,
        )

        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        ) {
            items(hospitalList, key = { it.id }) { hospital ->
                Box(modifier = Modifier.padding(bottom = 4.dp, end = 4.dp)) {
                    HospitalCard(
                        hospital = hospital,
                        onHospitalClick = { executeIntent(HospitalIntent.OpenHospitalDetail(hospital.id)) },
                    )
                }
            }
            item {
                AddHospitalButton(executeIntent)
            }
        }
    }
}

private fun consumeSideEffects(
    sideEffect: HospitalSideEffect,
    navigateToCreateHospital: () -> Unit,
    navigateToEditHospital: (UUID) -> Unit,
) {
    when (sideEffect) {
        is HospitalSideEffect.OpenCreateHospital -> navigateToCreateHospital()
        is HospitalSideEffect.OpenHospitalDetail -> navigateToEditHospital(sideEffect.id)
    }
}
