package com.rodrirepresa.nursera.feature.hospital.data.di

import com.rodrirepresa.nursera.feature.hospital.data.repository.HospitalRepositoryImpl
import com.rodrirepresa.nursera.feature.hospital.domain.repository.HospitalRepository
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.AddShiftToHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.AddShiftToHospitalUseCaseImpl
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.CreateHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.CreateHospitalUseCaseImpl
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.DeleteHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.DeleteHospitalUseCaseImpl
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.DeleteShiftsFromHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.DeleteShiftsFromHospitalUseCaseImpl
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.GetHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.GetHospitalUseCaseImpl
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.GetRandomHospitalColorUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.GetRandomHospitalColorUseCaseImpl
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.ObserveHospitalByIdUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.ObserveHospitalByIdUseCaseImpl
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.ObserveHospitalsUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.ObserveHospitalsUseCaseImpl
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.UpdateHospitalIrpfUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.UpdateHospitalIrpfUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class HospitalDataModule {
    @Binds
    abstract fun bindHospitalRepository(impl: HospitalRepositoryImpl): HospitalRepository

    @Binds
    abstract fun bindObserveHospitalsUseCase(impl: ObserveHospitalsUseCaseImpl): ObserveHospitalsUseCase

    @Binds
    abstract fun bindCreateHospitalUseCase(impl: CreateHospitalUseCaseImpl): CreateHospitalUseCase

    @Binds
    abstract fun bindDeleteHospitalUseCase(impl: DeleteHospitalUseCaseImpl): DeleteHospitalUseCase

    @Binds
    abstract fun bindGetRandomHospitalColorUseCase(
        impl: GetRandomHospitalColorUseCaseImpl,
    ): GetRandomHospitalColorUseCase

    @Binds
    abstract fun bindGetHospitalUseCase(impl: GetHospitalUseCaseImpl): GetHospitalUseCase

    @Binds
    abstract fun bindAddShiftToHospitalUseCase(impl: AddShiftToHospitalUseCaseImpl): AddShiftToHospitalUseCase

    @Binds
    abstract fun bindUpdateHospitalIrpfUseCase(impl: UpdateHospitalIrpfUseCaseImpl): UpdateHospitalIrpfUseCase

    @Binds
    abstract fun bindObserveHospitalByIdUseCase(impl: ObserveHospitalByIdUseCaseImpl): ObserveHospitalByIdUseCase

    @Binds
    abstract fun bindDeleteShiftsFromHospitalUseCase(
        impl: DeleteShiftsFromHospitalUseCaseImpl,
    ): DeleteShiftsFromHospitalUseCase
}
