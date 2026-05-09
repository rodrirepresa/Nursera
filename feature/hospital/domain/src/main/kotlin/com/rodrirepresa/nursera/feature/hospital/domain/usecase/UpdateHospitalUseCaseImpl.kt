package com.rodrirepresa.nursera.feature.hospital.domain.usecase

import com.rodrirepresa.nursera.feature.hospital.domain.model.ShiftType
import com.rodrirepresa.nursera.feature.hospital.domain.repository.HospitalRepository
import java.util.UUID
import javax.inject.Inject

class UpdateHospitalUseCaseImpl
    @Inject
    constructor(
        private val repository: HospitalRepository,
    ) : UpdateHospitalUseCase {
        override suspend fun invoke(
            id: UUID,
            irpf: Float,
            additionalShifts: List<ShiftType>,
        ) {
            repository.updateHospital(id, irpf, additionalShifts)
        }
    }
