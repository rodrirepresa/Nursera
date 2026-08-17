package com.rodrirepresa.nursera.feature.schedule.domain.usecase

import com.rodrirepresa.nursera.feature.schedule.domain.model.ScheduledShift
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

interface AddScheduledShiftUseCase {
    suspend operator fun invoke(
        date: LocalDate,
        hospitalId: UUID,
        hospitalName: String,
        hospitalColor: Int,
        shiftName: String,
        startTime: LocalTime,
    ): ScheduledShift
}
