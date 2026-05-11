package com.rodrirepresa.nursera.feature.hospital.domain.repository

import com.rodrirepresa.nursera.feature.hospital.domain.model.Hospital
import com.rodrirepresa.nursera.feature.hospital.domain.model.ShiftType
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime
import java.util.UUID

interface HospitalRepository {
    fun observeHospitals(): Flow<List<Hospital>>

    fun observeHospital(id: UUID): Flow<Hospital>

    suspend fun createHospital(
        name: String,
        color: Int,
        irpf: Float,
        shifts: List<ShiftType>,
    )

    suspend fun deleteHospital(id: UUID)

    suspend fun getHospital(id: UUID): Hospital?

    suspend fun updateHospitalIrpf(
        id: UUID,
        irpf: Float,
    )

    suspend fun addShift(
        hospitalId: UUID,
        name: String,
        startTime: LocalTime,
        endTime: LocalTime,
        hourlyRate: Double,
    )

    suspend fun deleteShifts(
        hospitalId: UUID,
        shiftsIds: List<UUID>,
    )
}
