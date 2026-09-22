package com.rodrirepresa.nursera.feature.hospital.presentation.edit.validators

import com.rodrirepresa.nursera.feature.hospital.presentation.R
import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState

internal fun ShiftFormUiState.withValidation(): ShiftFormUiState =
    copy(
        nameError =
            when {
                name.isEmpty() -> null
                name.length > 60 -> R.string.validation_shift_name_too_long
                else -> null
            },
        startTimeError =
            when {
                startTime.isEmpty() -> null
                !isValidTime(startTime) -> R.string.validation_time_format
                endTime.isNotEmpty() && isValidTime(endTime) &&
                    startTime >= endTime -> R.string.validation_start_before_end

                else -> null
            },
        endTimeError =
            when {
                endTime.isEmpty() -> null
                !isValidTime(endTime) -> R.string.validation_time_format
                startTime.isNotEmpty() && isValidTime(startTime) &&
                    endTime <= startTime -> R.string.validation_end_after_start

                else -> null
            },
        hourlyRateError =
            when {
                hourlyRate.isEmpty() -> null
                hourlyRate.toDoubleOrNull() == null -> R.string.validation_invalid_number
                hourlyRate.toDouble() <= 0 -> R.string.validation_rate_must_be_positive
                else -> null
            },
    )

internal fun ShiftFormUiState.isValid(): Boolean =
    name.isNotBlank() &&
        name.length <= 60 &&
        isValidTime(startTime) &&
        isValidTime(endTime) &&
        startTime < endTime &&
        hourlyRate.toDoubleOrNull()?.let { it > 0 } == true &&
        nameError == null && startTimeError == null && endTimeError == null && hourlyRateError == null
