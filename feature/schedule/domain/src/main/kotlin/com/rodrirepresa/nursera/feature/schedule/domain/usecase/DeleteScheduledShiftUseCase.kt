package com.rodrirepresa.nursera.feature.schedule.domain.usecase

import java.util.UUID

interface DeleteScheduledShiftUseCase {
    suspend operator fun invoke(id: UUID)
}
