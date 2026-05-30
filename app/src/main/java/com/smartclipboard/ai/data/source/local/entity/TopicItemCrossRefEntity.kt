package com.smartclipboard.ai.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "topic_item_cross_refs",
    primaryKeys = ["topicId", "dataItemId"],
    foreignKeys = [
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DataItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["dataItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("topicId"),
        Index("dataItemId"),
        Index("selectedBy")
    ]
)
data class TopicItemCrossRefEntity(
    val topicId: Long,
    val dataItemId: Long,
    val selectedBy: String,
    val createdAtMillis: Long
)
