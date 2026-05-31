package com.smartclipboard.ai.storage

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageCleanupManager @Inject constructor(
    private val store: StorageCleanupStore,
    private val policy: StorageQuotaPolicy
) {
    internal constructor(
        store: StorageCleanupStore,
        policy: StorageQuotaPolicy,
        nowMillis: () -> Long
    ) : this(store, policy) {
        this.nowMillis = nowMillis
    }

    private var nowMillis: () -> Long = { System.currentTimeMillis() }

    suspend fun calculateUsage(quotaBytes: Long): StorageUsageSummary {
        return policy.calculateUsage(
            items = store.getActiveItems(),
            quotaBytes = quotaBytes
        )
    }

    suspend fun cleanup(quotaBytes: Long): StorageCleanupResult {
        val activeItems = store.getActiveItems()
        val linkedIds = store.getTopicLinkedDataItemIds()
        val before = policy.calculateUsage(
            items = activeItems,
            quotaBytes = quotaBytes
        )
        val candidates = policy.selectCleanupCandidates(
            items = activeItems,
            topicLinkedItemIds = linkedIds,
            quotaBytes = quotaBytes
        )

        if (candidates.isEmpty()) {
            return StorageCleanupResult(
                before = before,
                candidates = emptyList(),
                deletedCount = 0,
                reclaimedBytes = 0L
            )
        }

        val deletedCount = store.softDeleteDataItems(
            itemIds = candidates.map { it.itemId },
            deletedAtMillis = nowMillis()
        )
        return StorageCleanupResult(
            before = before,
            candidates = candidates,
            deletedCount = deletedCount,
            reclaimedBytes = candidates.take(deletedCount).sumOf { it.reclaimableBytes }
        )
    }
}
