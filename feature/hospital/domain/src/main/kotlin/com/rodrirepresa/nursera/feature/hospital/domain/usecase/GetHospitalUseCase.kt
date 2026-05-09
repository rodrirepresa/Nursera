package com.rodrirepresa.nursera.feature.hospital.domain.usecase

import com.rodrirepresa.nursera.feature.hospital.domain.model.Hospital
import java.util.UUID

interface GetHospitalUseCase {
    suspend operator fun invoke(id: UUID): Hospital?
}
