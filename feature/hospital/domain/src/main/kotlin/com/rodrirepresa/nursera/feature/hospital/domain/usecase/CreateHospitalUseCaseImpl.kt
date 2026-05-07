package com.rodrirepresa.nursera.feature.hospital.domain.usecase

import com.rodrirepresa.nursera.feature.hospital.domain.model.ShiftType
import com.rodrirepresa.nursera.feature.hospital.domain.repository.HospitalRepository
import javax.inject.Inject

class CreateHospitalUseCaseImpl
    @Inject
    constructor(
        private val repository: HospitalRepository,
    ) : CreateHospitalUseCase {
        override suspend fun invoke(
            name: String,
            color: Int,
            irpf: Float,
            shifts: List<ShiftType>,
        ) {
            repository.createHospital(name, color, irpf, shifts)
        }
    }
