package com.rodrirepresa.nursera.feature.profile.presentation.viewmodel

import androidx.compose.runtime.Immutable

@Immutable
data class HospitalEarningsUiModel(
    val hospitalName: String,
    val hospitalColor: Int,
    val grossAmount: Double,
    val netAmount: Double,
    val irpf: Float,
    val percentage: Float,
    val shiftsCount: Int,
)
