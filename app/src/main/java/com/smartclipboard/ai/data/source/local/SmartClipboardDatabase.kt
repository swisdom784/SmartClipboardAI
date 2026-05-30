package com.smartclipboard.ai.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smartclipboard.ai.data.source.local.dao.DataItemDao
import com.smartclipboard.ai.data.source.local.dao.TopicActionDao
import com.smartclipboard.ai.data.source.local.dao.TopicAnalysisDao
import com.smartclipboard.ai.data.source.local.dao.TopicDao
import com.smartclipboard.ai.data.source.local.entity.DataItemEntity
import com.smartclipboard.ai.data.source.local.entity.TopicActionEntity
import com.smartclipboard.ai.data.source.local.entity.TopicAnalysisEntity
import com.smartclipboard.ai.data.source.local.entity.TopicEntity
import com.smartclipboard.ai.data.source.local.entity.TopicItemCrossRefEntity

@Database(
    entities = [
        DataItemEntity::class,
        TopicEntity::class,
        TopicItemCrossRefEntity::class,
        TopicAnalysisEntity::class,
        TopicActionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SmartClipboardDatabase : RoomDatabase() {
    abstract fun dataItemDao(): DataItemDao
    abstract fun topicDao(): TopicDao
    abstract fun topicAnalysisDao(): TopicAnalysisDao
    abstract fun topicActionDao(): TopicActionDao
}
