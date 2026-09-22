package com.rodrirepresa.nursera.feature.hospital.presentation.list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adidas.mvi.compose.MviContainer
import com.rodrirepresa.nursera.core.ui.NurseraHeader
import com.rodrirepresa.nursera.core.ui.NurseraLoadingView
import com.rodrirepresa.nursera.feature.hospital.presentation.R
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
        when (state) {
            is HospitalState.Loading -> NurseraLoadingView()

            is HospitalState.Loaded -> {
                HospitalScreenLoaded(
                    hospitalList = state.hospitals,
                    executeIntent = viewModel::execute,
                )
            }
        }
    }
}

@Composable
private fun HospitalScreenLoaded(
    hospitalList: ImmutableList<HospitalUiModel>,
    executeIntent: (HospitalIntent) -> Unit,
) {
    Column {
        val label =
            if (hospitalList.isEmpty()) {
                stringResource(R.string.hospital_list_empty_subtitle)
            } else {
                pluralStringResource(
                    R.plurals.hospital_list_count_subtitle,
                    hospitalList.size,
                    hospitalList.size,
                )
            }

        NurseraHeader(
            title = stringResource(R.string.hospital_list_title),
            subtitle = label,
            onIconClick = { executeIntent(HospitalIntent.OpenCreateHospital) },
            icon = Icons.Filled.Add,
            iconContentDescription = stringResource(R.string.hospital_list_add_content_description),
        )

        LazyColumn(
            modifier =
                Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                    )
                    .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
        ) {
            items(items = hospitalList, key = { it.id }) { hospital ->
                Box(modifier = Modifier.padding(bottom = 4.dp, end = 4.dp)) {
                    HospitalCard(
                        hospital = hospital,
                        onHospitalClick = { executeIntent(HospitalIntent.OpenHospitalDetail(hospital.id)) },
                    )
                }
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
