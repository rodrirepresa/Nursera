package com.rodrirepresa.nursera.feature.earnings.presentation.ui

import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale

internal fun monthFormatter(locale: Locale): DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)

/**
 * Amounts are always paid in euros, but grouping and symbol placement follow the device locale.
 */
internal fun euroFormat(locale: Locale): NumberFormat = NumberFormat.getCurrencyInstance(locale).apply { currency = Currency.getInstance("EUR") }
