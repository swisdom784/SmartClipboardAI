package com.smartclipboard.ai.storage

data class StorageCleanupCandidate(
    val itemId: Long,
    val reclaimableBytes: Long,
    val reason: StorageCleanupReason
)

enum class StorageCleanupReason {
    INTERNAL_COPY,
    UNLINKED_OLD_DATA_ITEM
}
