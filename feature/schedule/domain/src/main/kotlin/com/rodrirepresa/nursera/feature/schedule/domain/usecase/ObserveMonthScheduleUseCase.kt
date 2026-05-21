package com.rodrirepresa.nursera.feature.schedule.domain.usecase

import com.rodrirepresa.nursera.feature.schedule.domain.model.ScheduledShift
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

interface ObserveMonthScheduleUseCase {
    operator fun invoke(month: YearMonth): Flow<List<ScheduledShift>>
}
