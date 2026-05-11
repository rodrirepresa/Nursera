package com.rodrirepresa.nursera.feature.hospital.presentation.list.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rodrirepresa.nursera.core.ui.NurseraCta
import com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel.HospitalIntent

@Composable
internal fun AddHospitalButton(
    executeIntent: (HospitalIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    NurseraCta(
        label = "Añadir Hospital",
        onClick = { executeIntent(HospitalIntent.OpenCreateHospital) },
        modifier = modifier,
    )
}
