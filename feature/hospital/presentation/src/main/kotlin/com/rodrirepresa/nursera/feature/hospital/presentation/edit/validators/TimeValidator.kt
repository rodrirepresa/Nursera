package com.rodrirepresa.nursera.feature.hospital.presentation.edit.validators

import java.time.format.DateTimeFormatter

internal val timeRegex = Regex("""^([01]\d|2[0-3]):[0-5]\d$""")
internal val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

internal fun isValidTime(value: String) = timeRegex.matches(value)
