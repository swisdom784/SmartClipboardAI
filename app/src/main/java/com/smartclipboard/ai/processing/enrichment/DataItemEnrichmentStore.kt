package com.smartclipboard.ai.processing.enrichment

import com.smartclipboard.ai.data.source.local.dao.DataItemDao
import com.smartclipboard.ai.data.source.local.mapper.toDomain
import com.smartclipboard.ai.data.source.local.mapper.toEntity
import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemType
import javax.inject.Inject
import javax.inject.Singleton

interface DataItemEnrichmentStore {
    suspend fun getPendingItems(limit: Int): List<DataItem>
    suspend fun updateDataItem(item: DataItem)
}

@Singleton
class RoomDataItemEnrichmentStore @Inject constructor(
    private val dataItemDao: DataItemDao
) : DataItemEnrichmentStore {
    override suspend fun getPendingItems(limit: Int): List<DataItem> {
        return dataItemDao.getPendingForEnrichment(
            types = ENRICHABLE_TYPES,
            maxRetries = MAX_RETRIES,
            limit = limit
        ).map { it.toDomain() }
    }

    override suspend fun updateDataItem(item: DataItem) {
        dataItemDao.update(item.toEntity())
    }

    private companion object {
        const val MAX_RETRIES = 3
        val ENRICHABLE_TYPES = listOf(
            DataItemType.LINK,
            DataItemType.IMAGE,
            DataItemType.SCREENSHOT,
            DataItemType.DOWNLOAD_IMAGE
        ).map { it.name }
    }
}
