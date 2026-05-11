package com.rodrirepresa.nursera.feature.hospital.presentation.edit.validators

import com.rodrirepresa.nursera.feature.hospital.presentation.create.model.ShiftFormUiState

internal fun ShiftFormUiState.withValidation(): ShiftFormUiState =
    copy(
        nameError =
            when {
                name.isEmpty() -> null
                name.length > 60 -> "Máximo 60 caracteres"
                else -> null
            },
        startTimeError =
            when {
                startTime.isEmpty() -> null
                !isValidTime(startTime) -> "Formato HH:mm"
                endTime.isNotEmpty() && isValidTime(endTime) &&
                    startTime >= endTime -> "Debe ser anterior al fin"

                else -> null
            },
        endTimeError =
            when {
                endTime.isEmpty() -> null
                !isValidTime(endTime) -> "Formato HH:mm"
                startTime.isNotEmpty() && isValidTime(startTime) &&
                    endTime <= startTime -> "Debe ser posterior al inicio"

                else -> null
            },
        hourlyRateError =
            when {
                hourlyRate.isEmpty() -> null
                hourlyRate.toDoubleOrNull() == null -> "Número inválido"
                hourlyRate.toDouble() <= 0 -> "Debe ser mayor que 0"
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
