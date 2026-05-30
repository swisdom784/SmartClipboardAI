package com.smartclipboard.ai.data.source.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "data_items",
    indices = [
        Index("type"),
        Index("source"),
        Index("sourceUri"),
        Index("capturedAtMillis"),
        Index("deletedAtMillis")
    ]
)
data class DataItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val type: String,
    val source: String,
    val textContent: String? = null,
    val sourceUri: String? = null,
    val internalUri: String? = null,
    val capturedAtMillis: Long,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
    val lastSyncedAtMillis: Long? = null,
    val mimeType: String? = null,
    val displayName: String? = null,
    val sizeBytes: Long? = null,
    val mediaStoreId: Long? = null,
    val enrichmentStatus: String = "PENDING",
    val enrichmentRetryCount: Int = 0,
    val ocrText: String? = null,
    val ogTitle: String? = null,
    val ogDescription: String? = null,
    val ogImageUrl: String? = null,
    val geminiSummary: String? = null,
    val keywords: String? = null,
    val detectedDateTimeMillis: Long? = null,
    val detectedLocation: String? = null,
    val clusterId: String? = null,
    val clusterLabel: String? = null,
    val clusterScore: Double? = null,
    val clusterUpdatedAtMillis: Long? = null,
    val isImportant: Boolean = false,
    val isPreserved: Boolean = false,
    val deletedAtMillis: Long? = null
)
