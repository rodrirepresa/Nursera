package com.rodrirepresa.nursera.feature.profile.presentation.viewmodel

import com.adidas.mvi.Intent
import java.time.YearMonth

sealed interface ProfileIntent : Intent {
    data object Load : ProfileIntent

    data class SetDisplayedMonth(
        val month: YearMonth,
    ) : ProfileIntent
}
