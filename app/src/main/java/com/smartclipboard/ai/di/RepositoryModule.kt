package com.smartclipboard.ai.di

import com.smartclipboard.ai.data.repository.DataRepositoryImpl
import com.smartclipboard.ai.domain.repository.DataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindDataRepository(
        repository: DataRepositoryImpl
    ): DataRepository
}
