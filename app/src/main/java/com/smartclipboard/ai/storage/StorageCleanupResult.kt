package com.smartclipboard.ai.storage

data class StorageCleanupResult(
    val before: StorageUsageSummary,
    val candidates: List<StorageCleanupCandidate>,
    val deletedCount: Int,
    val reclaimedBytes: Long
)
