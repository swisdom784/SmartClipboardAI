package com.smartclipboard.ai.storage

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class StorageCleanupManagerTest {
    @Test
    fun `cleanup soft deletes selected data items without deleting media originals`() = runBlocking {
        val store = FakeStorageCleanupStore(
            items = listOf(
                dataItem(id = 1L, sizeBytes = 900L, capturedAtMillis = 1L),
                dataItem(id = 2L, sizeBytes = 900L, capturedAtMillis = 2L)
            )
        )
        val manager = StorageCleanupManager(
            store = store,
            policy = StorageQuotaPolicy(),
            nowMillis = { 9_000L }
        )

        val result = manager.cleanup(quotaBytes = 1_000L)

        assertEquals(listOf(1L), store.softDeletedIds)
        assertEquals(emptyList<String>(), store.deletedOriginalUris)
        assertEquals(1, result.deletedCount)
        assertEquals(900L, result.reclaimedBytes)
    }

    @Test
    fun `cleanup does nothing when usage is under quota`() = runBlocking {
        val store = FakeStorageCleanupStore(
            items = listOf(dataItem(id = 1L, sizeBytes = 400L))
        )
        val manager = StorageCleanupManager(
            store = store,
            policy = StorageQuotaPolicy()
        )

        val result = manager.cleanup(quotaBytes = 1_000L)

        assertEquals(emptyList<Long>(), store.softDeletedIds)
        assertEquals(0, result.deletedCount)
        assertEquals(0L, result.reclaimedBytes)
    }

    private fun dataItem(
        id: Long,
        sizeBytes: Long,
        capturedAtMillis: Long = 1_000L
    ): DataItem {
        return DataItem(
            id = id,
            type = DataItemType.IMAGE,
            source = DataItemSource.MEDIASTORE,
            sourceUri = "content://media/$id",
            capturedAtMillis = capturedAtMillis,
            createdAtMillis = capturedAtMillis,
            updatedAtMillis = capturedAtMillis,
            sizeBytes = sizeBytes
        )
    }

    private class FakeStorageCleanupStore(
        private val items: List<DataItem>,
        private val linkedIds: Set<Long> = emptySet()
    ) : StorageCleanupStore {
        val softDeletedIds = mutableListOf<Long>()
        val deletedOriginalUris = mutableListOf<String>()

        override suspend fun getActiveItems(): List<DataItem> {
            return items
        }

        override suspend fun getTopicLinkedDataItemIds(): Set<Long> {
            return linkedIds
        }

        override suspend fun softDeleteDataItems(
            itemIds: List<Long>,
            deletedAtMillis: Long
        ): Int {
            softDeletedIds += itemIds
            return itemIds.size
        }
    }
}
