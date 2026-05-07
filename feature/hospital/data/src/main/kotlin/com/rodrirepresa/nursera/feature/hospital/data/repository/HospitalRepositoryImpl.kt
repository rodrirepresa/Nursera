package com.rodrirepresa.nursera.feature.hospital.data.repository

import com.rodrirepresa.nursera.feature.hospital.domain.model.Hospital
import com.rodrirepresa.nursera.feature.hospital.domain.model.ShiftType
import com.rodrirepresa.nursera.feature.hospital.domain.repository.HospitalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HospitalRepositoryImpl
    @Inject
    constructor() : HospitalRepository {
        private val hospitalsFlow = MutableStateFlow(seedHospitals())

        override fun observeHospitals(): Flow<List<Hospital>> = hospitalsFlow.asStateFlow()

        override suspend fun createHospital(
            name: String,
            color: Int,
            irpf: Float,
            shifts: List<ShiftType>,
        ) {
            val hospital =
                Hospital(
                    id = UUID.randomUUID(),
                    name = name,
                    color = color,
                    irpf = irpf,
                    shifts = shifts.map { it.copy(id = UUID.randomUUID()) },
                )
            hospitalsFlow.update { current -> current + hospital }
        }

        override suspend fun deleteHospital(id: UUID) {
            hospitalsFlow.update { current -> current.filter { it.id != id } }
        }
    }

private fun seedHospitals(): List<Hospital> =
    listOf(
        Hospital(
            id = UUID.randomUUID(),
            name = "Hospital La Paz",
            color = 0xFF1565C0.toInt(),
            irpf = 15.0f,
            shifts =
                listOf(
                    ShiftType(UUID.randomUUID(), "Mañana", LocalTime.of(8, 0), LocalTime.of(15, 0), 18.50),
                    ShiftType(UUID.randomUUID(), "Tarde", LocalTime.of(15, 0), LocalTime.of(22, 0), 19.00),
                    ShiftType(UUID.randomUUID(), "Noche", LocalTime.of(22, 0), LocalTime.of(8, 0), 23.00),
                ),
        ),
        Hospital(
            id = UUID.randomUUID(),
            name = "Hospital Gregorio Marañón",
            color = 0xFF2E7D32.toInt(),
            irpf = 17.5f,
            shifts =
                listOf(
                    ShiftType(UUID.randomUUID(), "Mañana", LocalTime.of(8, 0), LocalTime.of(15, 0), 17.75),
                    ShiftType(UUID.randomUUID(), "Noche", LocalTime.of(22, 0), LocalTime.of(8, 0), 22.50),
                ),
        ),
        Hospital(
            id = UUID.randomUUID(),
            name = "Clínica Universidad de Navarra",
            color = 0xFF6A1B9A.toInt(),
            irpf = 20.0f,
            shifts =
                listOf(
                    ShiftType(UUID.randomUUID(), "Mañana", LocalTime.of(8, 0), LocalTime.of(15, 0), 21.00),
                    ShiftType(UUID.randomUUID(), "Tarde", LocalTime.of(15, 0), LocalTime.of(22, 0), 21.50),
                    ShiftType(UUID.randomUUID(), "Noche", LocalTime.of(22, 0), LocalTime.of(8, 0), 26.00),
                    ShiftType(UUID.randomUUID(), "Fin de semana", LocalTime.of(8, 0), LocalTime.of(20, 0), 28.00),
                ),
        ),
    )
