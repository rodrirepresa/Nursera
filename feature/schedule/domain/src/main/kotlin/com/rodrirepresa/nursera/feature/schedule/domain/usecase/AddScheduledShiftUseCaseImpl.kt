package com.rodrirepresa.nursera.feature.schedule.domain.usecase

import com.rodrirepresa.nursera.feature.schedule.domain.repository.ScheduleRepository
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

class AddScheduledShiftUseCaseImpl
    @Inject
    constructor(
        private val repository: ScheduleRepository,
    ) : AddScheduledShiftUseCase {
        override suspend fun invoke(
            date: LocalDate,
            hospitalId: UUID,
            hospitalName: String,
            hospitalColor: Int,
            shiftName: String,
        ) = repository.addShift(
            date = date,
            hospitalId = hospitalId,
            hospitalName = hospitalName,
            hospitalColor = hospitalColor,
            shiftName = shiftName,
        )
    }
