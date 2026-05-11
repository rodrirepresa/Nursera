package com.rodrirepresa.nursera.feature.hospital.domain.usecase

import java.util.UUID

interface UpdateHospitalIrpfUseCase {
    suspend operator fun invoke(
        id: UUID,
        irpf: Float,
    )
}
