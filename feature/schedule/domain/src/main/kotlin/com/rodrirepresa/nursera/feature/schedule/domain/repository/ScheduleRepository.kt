package com.rodrirepresa.nursera.feature.schedule.domain.repository

import com.rodrirepresa.nursera.feature.schedule.domain.model.ScheduledShift
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth
import java.util.UUID

interface ScheduleRepository {
    fun observeMonth(month: YearMonth): Flow<List<ScheduledShift>>

    suspend fun addShift(
        date: java.time.LocalDate,
        hospitalId: UUID,
        hospitalName: String,
        hospitalColor: Int,
        shiftName: String,
    )
}
