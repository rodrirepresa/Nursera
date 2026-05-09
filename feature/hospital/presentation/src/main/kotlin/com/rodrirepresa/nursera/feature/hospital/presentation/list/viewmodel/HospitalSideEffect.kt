package com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel

import java.util.UUID

internal sealed class HospitalSideEffect {
    object OpenCreateHospital : HospitalSideEffect()

    data class OpenHospitalDetail(val id: UUID) : HospitalSideEffect()
}
