package com.rodrirepresa.nursera.feature.hospital.domain.usecase

import com.rodrirepresa.nursera.feature.hospital.domain.repository.HospitalRepository
import java.util.UUID
import javax.inject.Inject

class UpdateHospitalIrpfUseCaseImpl
    @Inject
    constructor(
        private val repository: HospitalRepository,
    ) : UpdateHospitalIrpfUseCase {
        override suspend fun invoke(
            id: UUID,
            irpf: Float,
        ) {
            repository.updateHospitalIrpf(id, irpf)
        }
    }
