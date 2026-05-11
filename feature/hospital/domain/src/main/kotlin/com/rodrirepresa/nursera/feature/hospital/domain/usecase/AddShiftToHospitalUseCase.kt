package com.rodrirepresa.nursera.feature.hospital.domain.usecase

import java.time.LocalTime
import java.util.UUID

interface AddShiftToHospitalUseCase {
    suspend operator fun invoke(
        hospitalId: UUID,
        name: String,
        startTime: LocalTime,
        endTime: LocalTime,
        hourlyRate: Double,
    )
}
