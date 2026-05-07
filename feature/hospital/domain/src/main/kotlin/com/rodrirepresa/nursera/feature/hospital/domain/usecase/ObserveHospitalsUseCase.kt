package com.rodrirepresa.nursera.feature.hospital.domain.usecase

import com.rodrirepresa.nursera.feature.hospital.domain.model.Hospital
import kotlinx.coroutines.flow.Flow

interface ObserveHospitalsUseCase {
    operator fun invoke(): Flow<List<Hospital>>
}
