package com.rodrirepresa.nursera.feature.hospital.data.repository

import com.rodrirepresa.nursera.feature.hospital.domain.model.Hospital
import com.rodrirepresa.nursera.feature.hospital.domain.model.ShiftType
import com.rodrirepresa.nursera.feature.hospital.domain.repository.HospitalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
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

        override fun observeHospital(id: UUID): Flow<Hospital> {
            return hospitalsFlow.map { hospitals ->
                hospitals.firstOrNull { hospital -> hospital.id == id }
                    ?: error("Hospital not found: $id")
            }
        }

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

        override suspend fun getHospital(id: UUID): Hospital? = hospitalsFlow.value.find { it.id == id }

        override suspend fun deleteShifts(
            hospitalId: UUID,
            shiftsIds: List<UUID>,
        ) {
            hospitalsFlow.update { current ->
                current.map { hospital ->
                    if (hospital.id == hospitalId) {
                        hospital.copy(
                            shifts = hospital.shifts.filter { shift -> shift.id !in shiftsIds },
                        )
                    } else {
                        hospital
                    }
                }
            }
        }

        override suspend fun updateHospitalIrpf(
            id: UUID,
            irpf: Float,
        ) {
            hospitalsFlow.update { current ->
                current.map { hospital ->
                    if (hospital.id == id) {
                        hospital.copy(
                            irpf = irpf,
                        )
                    } else {
                        hospital
                    }
                }
            }
        }

        override suspend fun addShift(
            hospitalId: UUID,
            name: String,
            startTime: LocalTime,
            endTime: LocalTime,
            hourlyRate: Double,
        ) {
            hospitalsFlow.update { current ->
                current.map { hospital ->
                    if (hospital.id == hospitalId) {
                        hospital.copy(
                            shifts =
                                hospital.shifts +
                                    ShiftType(
                                        id = UUID.randomUUID(),
                                        name = name,
                                        startTime = startTime,
                                        endTime = endTime,
                                        hourlyRate = hourlyRate,
                                    ),
                        )
                    } else {
                        hospital
                    }
                }
            }
        }

        private fun seedHospitals(): List<Hospital> =
            listOf(
                Hospital(
                    id = UUID.randomUUID(),
                    name = "Hospital La Paz",
                    color = 0xFFDAF5F0.toInt(),
                    irpf = 15.0f,
                    shifts =
                        listOf(
                            ShiftType(
                                UUID.randomUUID(),
                                "Mañana",
                                LocalTime.of(8, 0),
                                LocalTime.of(15, 0),
                                18.50,
                            ),
                            ShiftType(
                                UUID.randomUUID(),
                                "Tarde",
                                LocalTime.of(15, 0),
                                LocalTime.of(22, 0),
                                19.00,
                            ),
                            ShiftType(
                                UUID.randomUUID(),
                                "Noche",
                                LocalTime.of(22, 0),
                                LocalTime.of(8, 0),
                                23.00,
                            ),
                            ShiftType(
                                UUID.randomUUID(),
                                "Noche",
                                LocalTime.of(22, 0),
                                LocalTime.of(8, 0),
                                23.00,
                            ),
                            ShiftType(
                                UUID.randomUUID(),
                                "Noche",
                                LocalTime.of(22, 0),
                                LocalTime.of(8, 0),
                                23.00,
                            ),
                            ShiftType(
                                UUID.randomUUID(),
                                "Noche",
                                LocalTime.of(22, 0),
                                LocalTime.of(8, 0),
                                23.00,
                            ),
                            ShiftType(
                                UUID.randomUUID(),
                                "Noche",
                                LocalTime.of(22, 0),
                                LocalTime.of(8, 0),
                                23.00,
                            ),
                            ShiftType(
                                UUID.randomUUID(),
                                "Noche",
                                LocalTime.of(22, 0),
                                LocalTime.of(8, 0),
                                23.00,
                            ),
                            ShiftType(
                                UUID.randomUUID(),
                                "Noche",
                                LocalTime.of(22, 0),
                                LocalTime.of(8, 0),
                                23.00,
                            ),
                        ),
                ),
                Hospital(
                    id = UUID.randomUUID(),
                    name = "Hospital Gregorio Marañón",
                    color = 0xFFFDFDF6.toInt(),
                    irpf = 17.5f,
                    shifts =
                        listOf(
                            ShiftType(
                                UUID.randomUUID(),
                                "Mañana",
                                LocalTime.of(8, 0),
                                LocalTime.of(15, 0),
                                17.75,
                            ),
                            ShiftType(
                                UUID.randomUUID(),
                                "Noche",
                                LocalTime.of(22, 0),
                                LocalTime.of(8, 0),
                                22.50,
                            ),
                        ),
                ),
                Hospital(
                    id = UUID.randomUUID(),
                    name = "Clínica Universidad de Navarra",
                    color = 0xFFFCDFFF.toInt(),
                    irpf = 20.0f,
                    shifts =
                        listOf(
                            ShiftType(
                                UUID.randomUUID(),
                                "Mañana",
                                LocalTime.of(8, 0),
                                LocalTime.of(15, 0),
                                21.00,
                            ),
                            ShiftType(
                                UUID.randomUUID(),
                                "Tarde",
                                LocalTime.of(15, 0),
                                LocalTime.of(22, 0),
                                21.50,
                            ),
                            ShiftType(
                                UUID.randomUUID(),
                                "Noche",
                                LocalTime.of(22, 0),
                                LocalTime.of(8, 0),
                                26.00,
                            ),
                            ShiftType(
                                UUID.randomUUID(),
                                "Fin de semana",
                                LocalTime.of(8, 0),
                                LocalTime.of(20, 0),
                                28.00,
                            ),
                        ),
                ),
                Hospital(
                    id = UUID.randomUUID(),
                    name = "Clínica Universidad de Navarra",
                    color = 0xFFF8D6B3.toInt(),
                    irpf = 20.0f,
                    shifts =
                        listOf(
                            ShiftType(UUID.randomUUID(), "Mañana", LocalTime.of(8, 0), LocalTime.of(15, 0), 21.00),
                            ShiftType(UUID.randomUUID(), "Tarde", LocalTime.of(15, 0), LocalTime.of(22, 0), 21.50),
                            ShiftType(UUID.randomUUID(), "Noche", LocalTime.of(22, 0), LocalTime.of(8, 0), 26.00),
                            ShiftType(
                                UUID.randomUUID(),
                                "Fin de semana",
                                LocalTime.of(8, 0),
                                LocalTime.of(20, 0),
                                28.00,
                            ),
                        ),
                ),
                Hospital(
                    id = UUID.randomUUID(),
                    name = "Hospital Gregorio Marañón",
                    color = 0xFFFDFDF6.toInt(),
                    irpf = 17.5f,
                    shifts = emptyList(),
                ),
                Hospital(
                    id = UUID.randomUUID(),
                    name = "Hospital La Paz",
                    color = 0xFFDAF5F0.toInt(),
                    irpf = 15.0f,
                    shifts =
                        listOf(
                            ShiftType(UUID.randomUUID(), "Mañana", LocalTime.of(8, 0), LocalTime.of(15, 0), 18.50),
                            ShiftType(UUID.randomUUID(), "Tarde", LocalTime.of(15, 0), LocalTime.of(22, 0), 19.00),
                            ShiftType(UUID.randomUUID(), "Noche", LocalTime.of(22, 0), LocalTime.of(8, 0), 23.00),
                        ),
                ),
            )
    }
