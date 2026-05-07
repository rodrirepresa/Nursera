package com.rodrirepresa.nursera.feature.hospital.presentation.create.viewmodel

sealed class CreateHospitalSideEffect {
    data object NavigateBack : CreateHospitalSideEffect()
}
