package com.rodrirepresa.nursera.feature.hospital.domain.usecase

import com.rodrirepresa.nursera.feature.hospital.domain.model.Hospital
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ObserveHospitalByIdUseCase {
    operator fun invoke(id: UUID): Flow<Hospital>
}
