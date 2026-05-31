package com.smartclipboard.ai.storage

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemStorage
import com.smartclipboard.ai.domain.model.DataItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StorageQuotaPolicyTest {
    private val policy = StorageQuotaPolicy()

    @Test
    fun `usage summary sums active item sizes and quota overage`() {
        val summary = policy.calculateUsage(
            items = listOf(
                dataItem(id = 1L, sizeBytes = 700L),
                dataItem(id = 2L, sizeBytes = 500L),
                dataItem(id = 3L, sizeBytes = null, textContent = "abcd")
            ),
            quotaBytes = 1_000L
        )

        assertEquals(1_204L, summary.usedBytes)
        assertEquals(1_000L, summary.quotaBytes)
        assertEquals(204L, summary.overQuotaBytes)
        assertEquals(3, summary.itemCount)
    }

    @Test
    fun `protected and topic linked items are excluded from cleanup candidates`() {
        val candidates = policy.selectCleanupCandidates(
            items = listOf(
                dataItem(id = 1L, sizeBytes = 700L, capturedAtMillis = 1L, isImportant = true),
                dataItem(id = 2L, sizeBytes = 700L, capturedAtMillis = 2L, isPreserved = true),
                dataItem(id = 3L, sizeBytes = 700L, capturedAtMillis = 3L),
                dataItem(id = 4L, sizeBytes = 700L, capturedAtMillis = 4L)
            ),
            topicLinkedItemIds = setOf(3L),
            quotaBytes = 1_000L
        )

        assertEquals(listOf(4L), candidates.map { it.itemId })
    }

    @Test
    fun `internal copies are selected before old metadata cleanup`() {
        val candidates = policy.selectCleanupCandidates(
            items = listOf(
                dataItem(id = 1L, sizeBytes = 800L, capturedAtMillis = 1L),
                dataItem(
                    id = 2L,
                    sizeBytes = 800L,
                    capturedAtMillis = 2L,
                    internalUri = "file://internal/newer-copy.jpg"
                ),
                dataItem(id = 3L, sizeBytes = 800L, capturedAtMillis = 3L)
            ),
            topicLinkedItemIds = emptySet(),
            quotaBytes = 1_000L
        )

        assertEquals(
            listOf(StorageCleanupReason.INTERNAL_COPY, StorageCleanupReason.UNLINKED_OLD_DATA_ITEM),
            candidates.take(2).map { it.reason }
        )
        assertEquals(listOf(2L, 1L), candidates.take(2).map { it.itemId })
        assertTrue(candidates.sumOf { it.reclaimableBytes } >= 1_400L)
    }

    private fun dataItem(
        id: Long,
        sizeBytes: Long?,
        capturedAtMillis: Long = 1_000L,
        textContent: String? = null,
        internalUri: String? = null,
        isImportant: Boolean = false,
        isPreserved: Boolean = false
    ): DataItem {
        return DataItem(
            id = id,
            type = DataItemType.IMAGE,
            source = DataItemSource.MEDIASTORE,
            textContent = textContent,
            sourceUri = "content://media/$id",
            capturedAtMillis = capturedAtMillis,
            createdAtMillis = capturedAtMillis,
            updatedAtMillis = capturedAtMillis,
            sizeBytes = sizeBytes,
            storage = DataItemStorage(
                internalUri = internalUri,
                isImportant = isImportant,
                isPreserved = isPreserved
            )
        )
    }
}
