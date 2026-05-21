package com.rodrirepresa.nursera.feature.schedule.data.di

import com.rodrirepresa.nursera.feature.schedule.data.repository.ScheduleRepositoryImpl
import com.rodrirepresa.nursera.feature.schedule.domain.repository.ScheduleRepository
import com.rodrirepresa.nursera.feature.schedule.domain.usecase.AddScheduledShiftUseCase
import com.rodrirepresa.nursera.feature.schedule.domain.usecase.AddScheduledShiftUseCaseImpl
import com.rodrirepresa.nursera.feature.schedule.domain.usecase.ObserveMonthScheduleUseCase
import com.rodrirepresa.nursera.feature.schedule.domain.usecase.ObserveMonthScheduleUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ScheduleDataModule {
    @Binds
    abstract fun bindScheduleRepository(impl: ScheduleRepositoryImpl): ScheduleRepository

    @Binds
    abstract fun bindObserveMonthScheduleUseCase(impl: ObserveMonthScheduleUseCaseImpl): ObserveMonthScheduleUseCase

    @Binds
    abstract fun bindAddScheduledShiftUseCase(impl: AddScheduledShiftUseCaseImpl): AddScheduledShiftUseCase
}
