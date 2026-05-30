package com.smartclipboard.ai.data.source.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "topics",
    indices = [
        Index("origin"),
        Index("status"),
        Index("createdAtMillis")
    ]
)
data class TopicEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val prompt: String? = null,
    val origin: String,
    val status: String = "ACTIVE",
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
    val completedAtMillis: Long? = null
)
