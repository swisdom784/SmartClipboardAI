package com.smartclipboard.ai.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "topic_analyses",
    foreignKeys = [
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("topicId"),
        Index("status"),
        Index("createdAtMillis")
    ]
)
data class TopicAnalysisEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val topicId: Long,
    val status: String = "PENDING",
    val summary: String? = null,
    val evidence: String? = null,
    val modelName: String? = null,
    val failureReason: String? = null,
    val retryCount: Int = 0,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)
