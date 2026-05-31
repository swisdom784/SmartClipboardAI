package com.smartclipboard.ai.storage

data class StorageUsageSummary(
    val usedBytes: Long,
    val quotaBytes: Long,
    val overQuotaBytes: Long,
    val itemCount: Int
)
