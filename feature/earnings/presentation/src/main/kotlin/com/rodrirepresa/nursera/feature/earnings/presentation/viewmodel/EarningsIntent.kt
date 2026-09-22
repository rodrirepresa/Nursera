package com.rodrirepresa.nursera.feature.earnings.presentation.viewmodel

import com.adidas.mvi.Intent
import java.time.YearMonth

sealed interface EarningsIntent : Intent {
    data object Load : EarningsIntent

    data class SetDisplayedMonth(
        val month: YearMonth,
    ) : EarningsIntent
}
