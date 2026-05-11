package com.rodrirepresa.nursera.feature.hospital.domain.usecase

import com.rodrirepresa.nursera.feature.hospital.domain.repository.HospitalRepository
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

class AddShiftToHospitalUseCaseImpl
    @Inject
    constructor(
        private val repository: HospitalRepository,
    ) : AddShiftToHospitalUseCase {
        override suspend fun invoke(
            hospitalId: UUID,
            name: String,
            startTime: LocalTime,
            endTime: LocalTime,
            hourlyRate: Double,
        ) {
            repository.addShift(
                hospitalId = hospitalId,
                name = name,
                startTime = startTime,
                endTime = endTime,
                hourlyRate = hourlyRate,
            )
        }
    }
