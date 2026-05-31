package com.smartclipboard.ai.di

import com.smartclipboard.ai.storage.RoomStorageCleanupStore
import com.smartclipboard.ai.storage.StorageCleanupStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {
    @Binds
    @Singleton
    abstract fun bindStorageCleanupStore(
        store: RoomStorageCleanupStore
    ): StorageCleanupStore
}
