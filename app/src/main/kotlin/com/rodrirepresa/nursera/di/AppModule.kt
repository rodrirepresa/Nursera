package com.rodrirepresa.nursera.di

import com.rodrirepresa.nursera.core.common.DefaultDispatcherProvider
import com.rodrirepresa.nursera.core.common.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider {
        return DefaultDispatcherProvider
    }
}
