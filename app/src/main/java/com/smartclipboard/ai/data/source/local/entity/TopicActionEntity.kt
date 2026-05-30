package com.smartclipboard.ai.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "topic_actions",
    foreignKeys = [
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TopicAnalysisEntity::class,
            parentColumns = ["id"],
            childColumns = ["analysisId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("topicId"),
        Index("analysisId"),
        Index("type"),
        Index("status"),
        Index("targetApp")
    ]
)
data class TopicActionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val topicId: Long,
    val analysisId: Long? = null,
    val type: String,
    val status: String = "PENDING_REVIEW",
    val targetApp: String = "NONE",
    val title: String,
    val body: String,
    val previewText: String,
    val scheduledStartAtMillis: Long? = null,
    val scheduledEndAtMillis: Long? = null,
    val isAllDay: Boolean = false,
    val location: String? = null,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
    val completedAtMillis: Long? = null
)
