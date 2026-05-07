package com.rodrirepresa.nursera.feature.hospital.data.di

import com.rodrirepresa.nursera.feature.hospital.data.repository.HospitalRepositoryImpl
import com.rodrirepresa.nursera.feature.hospital.domain.repository.HospitalRepository
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.CreateHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.CreateHospitalUseCaseImpl
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.DeleteHospitalUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.DeleteHospitalUseCaseImpl
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.ObserveHospitalsUseCase
import com.rodrirepresa.nursera.feature.hospital.domain.usecase.ObserveHospitalsUseCaseImpl
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
}
