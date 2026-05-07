package com.rodrirepresa.nursera.feature.hospital.domain.usecase

import com.rodrirepresa.nursera.feature.hospital.domain.model.ShiftType

interface CreateHospitalUseCase {
    suspend operator fun invoke(
        name: String,
        color: Int,
        irpf: Float,
        shifts: List<ShiftType>,
    )
}
