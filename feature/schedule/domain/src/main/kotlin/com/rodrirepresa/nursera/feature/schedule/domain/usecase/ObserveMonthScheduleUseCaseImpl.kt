package com.rodrirepresa.nursera.feature.schedule.domain.usecase

import com.rodrirepresa.nursera.feature.schedule.domain.model.ScheduledShift
import com.rodrirepresa.nursera.feature.schedule.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth
import javax.inject.Inject

class ObserveMonthScheduleUseCaseImpl
    @Inject
    constructor(
        private val repository: ScheduleRepository,
    ) : ObserveMonthScheduleUseCase {
        override fun invoke(month: YearMonth): Flow<List<ScheduledShift>> = repository.observeMonth(month)
    }
