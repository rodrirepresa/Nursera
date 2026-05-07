package com.rodrirepresa.nursera.feature.hospital.presentation.list.ui

import com.rodrirepresa.nursera.feature.hospital.domain.model.Hospital
import com.rodrirepresa.nursera.feature.hospital.domain.model.ShiftType
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun Hospital.toUiModel() =
    HospitalUiModel(
        id = id,
        name = name,
        color = color,
        irpf = "$irpf%",
        shifts = shifts.map { it.toUiModel() },
    )

private fun ShiftType.toUiModel() =
    ShiftTypeUiModel(
        name = name,
        schedule = "${startTime.format(timeFormatter)}–${endTime.format(timeFormatter)}",
        hourlyRate = "${"%.2f".format(hourlyRate)}€/h",
    )

fun ShiftTypeFormData.toDomain() =
    ShiftType(
        id = UUID.randomUUID(),
        name = name,
        startTime = LocalTime.parse(startTime, timeFormatter),
        endTime = LocalTime.parse(endTime, timeFormatter),
        hourlyRate = hourlyRate.toDouble(),
    )
