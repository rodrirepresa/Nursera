package com.rodrirepresa.nursera.feature.earnings.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.rodrirepresa.nursera.core.ui.currentLocale
import java.time.YearMonth

@Composable
@ReadOnlyComposable
internal fun YearMonth.toUiTitle(): String {
    val locale = currentLocale()
    return monthFormatter(locale).format(this).replaceFirstChar { it.uppercase(locale) }
}
