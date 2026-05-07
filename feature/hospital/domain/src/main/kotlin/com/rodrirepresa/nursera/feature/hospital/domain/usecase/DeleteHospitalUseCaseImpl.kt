package com.rodrirepresa.nursera.feature.hospital.domain.usecase

import com.rodrirepresa.nursera.feature.hospital.domain.repository.HospitalRepository
import java.util.UUID
import javax.inject.Inject

class DeleteHospitalUseCaseImpl
    @Inject
    constructor(
        private val repository: HospitalRepository,
    ) : DeleteHospitalUseCase {
        override suspend fun invoke(id: UUID) {
            repository.deleteHospital(id)
        }
    }
