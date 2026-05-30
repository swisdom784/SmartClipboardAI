package com.smartclipboard.ai.domain.model

data class DataItem(
    val id: Long = 0L,
    val type: DataItemType,
    val source: DataItemSource,
    val textContent: String? = null,
    val sourceUri: String? = null,
    val capturedAtMillis: Long,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
    val lastSyncedAtMillis: Long? = null,
    val mimeType: String? = null,
    val displayName: String? = null,
    val sizeBytes: Long? = null,
    val mediaStoreId: Long? = null,
    val storage: DataItemStorage = DataItemStorage(),
    val enrichment: DataItemEnrichment = DataItemEnrichment(),
    val cluster: DataItemCluster? = null,
    val deletedAtMillis: Long? = null
)

enum class DataItemType {
    TEXT,
    LINK,
    IMAGE,
    SCREENSHOT,
    DOWNLOAD_IMAGE,
    FILE
}

enum class DataItemSource {
    SHARE_TARGET,
    CLIPBOARD_TILE,
    MEDIASTORE,
    SAF,
    MANUAL
}

data class DataItemStorage(
    val internalUri: String? = null,
    val isImportant: Boolean = false,
    val isPreserved: Boolean = false
)

data class DataItemEnrichment(
    val status: EnrichmentStatus = EnrichmentStatus.PENDING,
    val retryCount: Int = 0,
    val ocrText: String? = null,
    val ogTitle: String? = null,
    val ogDescription: String? = null,
    val ogImageUrl: String? = null,
    val geminiSummary: String? = null,
    val keywords: List<String> = emptyList(),
    val detectedDateTimeMillis: Long? = null,
    val detectedLocation: String? = null
)

enum class EnrichmentStatus {
    PENDING,
    PROCESSING,
    DONE,
    FAILED
}

data class DataItemCluster(
    val id: String,
    val label: String? = null,
    val score: Double? = null,
    val updatedAtMillis: Long? = null
)
