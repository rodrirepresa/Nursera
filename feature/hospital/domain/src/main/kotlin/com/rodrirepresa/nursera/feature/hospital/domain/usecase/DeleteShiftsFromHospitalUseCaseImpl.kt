package com.rodrirepresa.nursera.feature.hospital.domain.usecase

import com.rodrirepresa.nursera.feature.hospital.domain.repository.HospitalRepository
import java.util.UUID
import javax.inject.Inject

class DeleteShiftsFromHospitalUseCaseImpl
    @Inject
    constructor(
        private val repository: HospitalRepository,
    ) : DeleteShiftsFromHospitalUseCase {
        override suspend fun invoke(
            hospitalId: UUID,
            shiftsIds: List<UUID>,
        ) {
            repository.deleteShifts(hospitalId, shiftsIds)
        }
    }
