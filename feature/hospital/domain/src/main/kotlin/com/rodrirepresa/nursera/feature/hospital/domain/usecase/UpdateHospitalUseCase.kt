package com.rodrirepresa.nursera.feature.hospital.domain.usecase

import com.rodrirepresa.nursera.feature.hospital.domain.model.ShiftType
import java.util.UUID

interface UpdateHospitalUseCase {
    suspend operator fun invoke(
        id: UUID,
        irpf: Float,
        additionalShifts: List<ShiftType>,
    )
}
