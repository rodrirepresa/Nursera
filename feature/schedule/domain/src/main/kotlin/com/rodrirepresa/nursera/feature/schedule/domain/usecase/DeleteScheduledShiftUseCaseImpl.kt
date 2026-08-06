package com.rodrirepresa.nursera.feature.schedule.domain.usecase

import com.rodrirepresa.nursera.feature.schedule.domain.repository.ScheduleRepository
import java.util.UUID
import javax.inject.Inject

class DeleteScheduledShiftUseCaseImpl
    @Inject
    constructor(
        private val repository: ScheduleRepository,
    ) : DeleteScheduledShiftUseCase {
        override suspend fun invoke(id: UUID) = repository.deleteShift(id)
    }
