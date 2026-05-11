package com.rodrirepresa.nursera.feature.hospital.domain.usecase

import java.util.UUID

interface DeleteShiftsFromHospitalUseCase {
    suspend operator fun invoke(
        hospitalId: UUID,
        shiftsIds: List<UUID>,
    )
}
