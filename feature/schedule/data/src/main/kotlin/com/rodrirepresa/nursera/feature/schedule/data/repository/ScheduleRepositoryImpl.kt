package com.rodrirepresa.nursera.feature.schedule.data.repository

import com.rodrirepresa.nursera.feature.schedule.domain.model.ScheduledShift
import com.rodrirepresa.nursera.feature.schedule.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleRepositoryImpl
    @Inject
    constructor() : ScheduleRepository {
        private val shiftsFlow = MutableStateFlow(seedShifts())

        override fun observeMonth(month: YearMonth): Flow<List<ScheduledShift>> =
            shiftsFlow.map { all -> all.filter { YearMonth.from(it.date) == month } }

        override suspend fun addShift(
            date: LocalDate,
            hospitalId: UUID,
            hospitalName: String,
            hospitalColor: Int,
            shiftName: String,
            startTime: LocalTime,
        ): ScheduledShift {
            val newShift =
                ScheduledShift(
                    id = UUID.randomUUID(),
                    date = date,
                    hospitalId = hospitalId,
                    hospitalName = hospitalName,
                    hospitalColor = hospitalColor,
                    shiftName = shiftName,
                    startTime = startTime,
                )
            shiftsFlow.update { current -> current + newShift }
            return newShift
        }

        override suspend fun deleteShift(id: UUID) {
            shiftsFlow.update { current -> current.filterNot { it.id == id } }
        }

        private fun seedShifts(): List<ScheduledShift> {
            val now = YearMonth.now()
            val lapaz = UUID.randomUUID()
            val maranon = UUID.randomUUID()
            val navarra = UUID.randomUUID()
            val quiron = UUID.randomUUID()
            val lafe = UUID.randomUUID()
            val ramon = UUID.randomUUID()
            return buildList {
                // Hospital La Paz — turquoise
                add(shift(now.atDay(2), lapaz, "Hospital La Paz", 0xFFDAF5F0.toInt(), "Morning"))
                add(shift(now.atDay(5), lapaz, "Hospital La Paz", 0xFFDAF5F0.toInt(), "Afternoon"))
                add(shift(now.atDay(9), lapaz, "Hospital La Paz", 0xFFDAF5F0.toInt(), "Night"))
                add(shift(now.atDay(12), lapaz, "Hospital La Paz", 0xFFDAF5F0.toInt(), "Morning"))
                add(shift(now.atDay(16), lapaz, "Hospital La Paz", 0xFFDAF5F0.toInt(), "Afternoon"))
                add(shift(now.atDay(26), lapaz, "Hospital La Paz", 0xFFDAF5F0.toInt(), "Afternoon"))
                // Gregorio Marañón Hospital — light blue
                add(shift(now.atDay(3), maranon, "H. G. Marañón", 0xFF90CAF9.toInt(), "Morning"))
                add(shift(now.atDay(7), maranon, "H. G. Marañón", 0xFF90CAF9.toInt(), "Night"))
                add(shift(now.atDay(14), maranon, "H. G. Marañón", 0xFF90CAF9.toInt(), "Morning"))
                add(shift(now.atDay(21), maranon, "H. G. Marañón", 0xFF90CAF9.toInt(), "Night"))
                add(shift(now.atDay(28), maranon, "H. G. Marañón", 0xFF90CAF9.toInt(), "Morning"))
                // Navarra University Clinic — purple
                add(shift(now.atDay(5), navarra, "C. Navarra", 0xFFC5A3FF.toInt(), "Weekend"))
                add(shift(now.atDay(12), navarra, "C. Navarra", 0xFFC5A3FF.toInt(), "Afternoon"))
                add(shift(now.atDay(26), navarra, "C. Navarra", 0xFFC5A3FF.toInt(), "Weekend"))
                // Quirón Hospital — peach
                add(shift(now.atDay(4), quiron, "Hospital Quirón", 0xFFFFCBA4.toInt(), "Morning"))
                add(shift(now.atDay(11), quiron, "Hospital Quirón", 0xFFFFCBA4.toInt(), "Afternoon"))
                add(shift(now.atDay(25), quiron, "Hospital Quirón", 0xFFFFCBA4.toInt(), "Weekend"))
                // Hospital La Fe — pink — overlaps with La Paz to exercise stacking
                add(shift(now.atDay(9), lafe, "Hospital La Fe", 0xFFFF8FAB.toInt(), "Morning"))
                add(shift(now.atDay(16), lafe, "Hospital La Fe", 0xFFFF8FAB.toInt(), "Night"))
                // Ramón y Cajal Hospital — yellow — overlaps with Navarra to exercise stacking
                add(shift(now.atDay(5), ramon, "H. Ramón y Cajal", 0xFFFFE566.toInt(), "Morning"))
                add(shift(now.atDay(12), ramon, "H. Ramón y Cajal", 0xFFFFE566.toInt(), "Afternoon"))
            }.filter { it.date.year == now.year && it.date.monthValue == now.monthValue }
        }

        private fun shift(
            date: LocalDate,
            hospitalId: UUID,
            hospitalName: String,
            hospitalColor: Int,
            shiftName: String,
            startTime: LocalTime = LocalTime.of(8, 0),
        ) = ScheduledShift(
            id = UUID.randomUUID(),
            date = date,
            hospitalId = hospitalId,
            hospitalName = hospitalName,
            hospitalColor = hospitalColor,
            shiftName = shiftName,
            startTime = startTime,
        )
    }
