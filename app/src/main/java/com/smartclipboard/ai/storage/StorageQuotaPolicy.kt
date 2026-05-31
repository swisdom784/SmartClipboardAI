package com.smartclipboard.ai.storage

import com.smartclipboard.ai.domain.model.DataItem
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class StorageQuotaPolicy @Inject constructor() {
    fun calculateUsage(
        items: List<DataItem>,
        quotaBytes: Long
    ): StorageUsageSummary {
        val usedBytes = items.sumOf { it.estimatedStorageBytes() }
        return StorageUsageSummary(
            usedBytes = usedBytes,
            quotaBytes = quotaBytes,
            overQuotaBytes = (usedBytes - quotaBytes).coerceAtLeast(0L),
            itemCount = items.size
        )
    }

    fun selectCleanupCandidates(
        items: List<DataItem>,
        topicLinkedItemIds: Set<Long>,
        quotaBytes: Long
    ): List<StorageCleanupCandidate> {
        val summary = calculateUsage(items, quotaBytes)
        if (summary.overQuotaBytes <= 0L) {
            return emptyList()
        }

        var selectedBytes = 0L
        val candidates = mutableListOf<StorageCleanupCandidate>()

        items.asSequence()
            .filterNot { it.isProtected(topicLinkedItemIds) }
            .filter { it.estimatedStorageBytes() > 0L }
            .sortedWith(cleanupOrder())
            .forEach { item ->
                if (selectedBytes >= summary.overQuotaBytes) {
                    return@forEach
                }

                val reclaimableBytes = item.estimatedStorageBytes()
                candidates += StorageCleanupCandidate(
                    itemId = item.id,
                    reclaimableBytes = reclaimableBytes,
                    reason = item.cleanupReason()
                )
                selectedBytes += reclaimableBytes
            }

        return candidates
    }

    private fun cleanupOrder(): Comparator<DataItem> {
        return compareBy<DataItem> { it.cleanupReason().priority }
            .thenBy { it.capturedAtMillis }
            .thenBy { it.id }
    }

    private fun DataItem.isProtected(topicLinkedItemIds: Set<Long>): Boolean {
        return storage.isImportant ||
            storage.isPreserved ||
            id in topicLinkedItemIds
    }

    private fun DataItem.cleanupReason(): StorageCleanupReason {
        return if (storage.internalUri != null) {
            StorageCleanupReason.INTERNAL_COPY
        } else {
            StorageCleanupReason.UNLINKED_OLD_DATA_ITEM
        }
    }

    private val StorageCleanupReason.priority: Int
        get() = when (this) {
            StorageCleanupReason.INTERNAL_COPY -> 0
            StorageCleanupReason.UNLINKED_OLD_DATA_ITEM -> 1
        }

    private fun DataItem.estimatedStorageBytes(): Long {
        return sizeBytes
            ?: listOfNotNull(
                textContent,
                enrichment.ocrText,
                enrichment.ogTitle,
                enrichment.ogDescription,
                enrichment.geminiSummary
            ).sumOf { it.toByteArray(StandardCharsets.UTF_8).size.toLong() }
    }
}
