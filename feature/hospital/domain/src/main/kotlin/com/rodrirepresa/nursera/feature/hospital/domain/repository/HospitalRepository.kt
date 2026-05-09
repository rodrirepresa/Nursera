package com.rodrirepresa.nursera.feature.hospital.domain.repository

import com.rodrirepresa.nursera.feature.hospital.domain.model.Hospital
import com.rodrirepresa.nursera.feature.hospital.domain.model.ShiftType
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface HospitalRepository {
    fun observeHospitals(): Flow<List<Hospital>>

    suspend fun createHospital(
        name: String,
        color: Int,
        irpf: Float,
        shifts: List<ShiftType>,
    )

    suspend fun deleteHospital(id: UUID)

    suspend fun getHospital(id: UUID): Hospital?

    suspend fun updateHospital(
        id: UUID,
        irpf: Float,
        additionalShifts: List<ShiftType>,
    )
}
