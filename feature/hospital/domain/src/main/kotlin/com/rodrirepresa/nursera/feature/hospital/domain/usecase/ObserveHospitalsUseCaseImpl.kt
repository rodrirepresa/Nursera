package com.rodrirepresa.nursera.feature.hospital.domain.usecase

import com.rodrirepresa.nursera.feature.hospital.domain.model.Hospital
import com.rodrirepresa.nursera.feature.hospital.domain.repository.HospitalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveHospitalsUseCaseImpl
    @Inject
    constructor(
        private val repository: HospitalRepository,
    ) : ObserveHospitalsUseCase {
        override fun invoke(): Flow<List<Hospital>> = repository.observeHospitals()
    }
