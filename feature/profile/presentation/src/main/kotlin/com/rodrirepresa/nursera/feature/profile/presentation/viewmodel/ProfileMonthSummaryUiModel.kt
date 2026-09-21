package com.rodrirepresa.nursera.feature.profile.presentation.viewmodel

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.YearMonth

@Immutable
data class ProfileMonthSummaryUiModel(
    val month: YearMonth,
    val totalGrossAmount: Double = 0.0,
    val totalNetAmount: Double = 0.0,
    val totalShifts: Int = 0,
    val hospitals: ImmutableList<HospitalEarningsUiModel> = persistentListOf(),
)
