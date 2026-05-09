package com.rodrirepresa.nursera.feature.hospital.domain.usecase

import com.rodrirepresa.nursera.feature.hospital.domain.model.Hospital
import com.rodrirepresa.nursera.feature.hospital.domain.repository.HospitalRepository
import java.util.UUID
import javax.inject.Inject

class GetHospitalUseCaseImpl
    @Inject
    constructor(
        private val repository: HospitalRepository,
    ) : GetHospitalUseCase {
        override suspend fun invoke(id: UUID): Hospital? = repository.getHospital(id)
    }
