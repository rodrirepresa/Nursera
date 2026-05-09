package com.rodrirepresa.nursera.feature.hospital.presentation.edit.viewmodel

sealed class EditHospitalSideEffect {
    data object NavigateBack : EditHospitalSideEffect()
}
