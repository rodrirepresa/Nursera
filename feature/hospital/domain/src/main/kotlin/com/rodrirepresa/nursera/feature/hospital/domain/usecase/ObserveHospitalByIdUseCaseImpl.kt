package com.rodrirepresa.nursera.feature.hospital.domain.usecase

import com.rodrirepresa.nursera.feature.hospital.domain.model.Hospital
import com.rodrirepresa.nursera.feature.hospital.domain.repository.HospitalRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class ObserveHospitalByIdUseCaseImpl
    @Inject
    constructor(
        private val repository: HospitalRepository,
    ) : ObserveHospitalByIdUseCase {
        override fun invoke(id: UUID): Flow<Hospital> = repository.observeHospital(id)
    }
