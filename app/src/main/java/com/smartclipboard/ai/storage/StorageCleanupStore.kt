package com.smartclipboard.ai.storage

import com.smartclipboard.ai.data.source.local.dao.DataItemDao
import com.smartclipboard.ai.data.source.local.dao.TopicDao
import com.smartclipboard.ai.data.source.local.mapper.toDomain
import com.smartclipboard.ai.domain.model.DataItem
import javax.inject.Inject
import javax.inject.Singleton

interface StorageCleanupStore {
    suspend fun getActiveItems(): List<DataItem>

    suspend fun getTopicLinkedDataItemIds(): Set<Long>

    suspend fun softDeleteDataItems(
        itemIds: List<Long>,
        deletedAtMillis: Long
    ): Int
}

@Singleton
class RoomStorageCleanupStore @Inject constructor(
    private val dataItemDao: DataItemDao,
    private val topicDao: TopicDao
) : StorageCleanupStore {
    override suspend fun getActiveItems(): List<DataItem> {
        return dataItemDao.getActiveItemsForStorageCleanup()
            .map { it.toDomain() }
    }

    override suspend fun getTopicLinkedDataItemIds(): Set<Long> {
        return topicDao.getLinkedDataItemIds().toSet()
    }

    override suspend fun softDeleteDataItems(
        itemIds: List<Long>,
        deletedAtMillis: Long
    ): Int {
        if (itemIds.isEmpty()) {
            return 0
        }

        return dataItemDao.softDeleteByIds(
            ids = itemIds,
            deletedAtMillis = deletedAtMillis
        )
    }
}
