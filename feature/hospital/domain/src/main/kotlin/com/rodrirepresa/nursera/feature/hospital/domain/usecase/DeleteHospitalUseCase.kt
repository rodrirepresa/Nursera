package com.rodrirepresa.nursera.feature.hospital.domain.usecase

import java.util.UUID

interface DeleteHospitalUseCase {
    suspend operator fun invoke(id: UUID)
}
