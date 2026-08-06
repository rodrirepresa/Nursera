package com.rodrirepresa.nursera.feature.schedule.domain.model

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class ScheduledShift(
    val id: UUID,
    val date: LocalDate,
    val hospitalId: UUID,
    val hospitalName: String,
    val hospitalColor: Int,
    val shiftName: String,
    val startTime: LocalTime,
)
