package com.rodrirepresa.nursera.feature.hospital.presentation.list.viewmodel

import com.adidas.mvi.Intent
import java.util.UUID

sealed interface HospitalIntent : Intent {
    data object Load : HospitalIntent

    data class OpenHospitalDetail(val id: UUID) : HospitalIntent

    data object OpenCreateHospital : HospitalIntent
}
