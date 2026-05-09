package com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel

internal sealed class HospitalSideEffect {
    object OpenCreateHospital : HospitalSideEffect()

    object OpenHospitalDetail : HospitalSideEffect()
}
