package com.rodrirepresa.nursera.feature.hospital.domain.model

import java.time.LocalTime
import java.util.UUID

data class ShiftType(
    val id: UUID,
    val name: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val hourlyRate: Double,
)
