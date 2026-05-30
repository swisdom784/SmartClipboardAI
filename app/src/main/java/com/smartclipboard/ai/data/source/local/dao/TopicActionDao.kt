package com.smartclipboard.ai.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.smartclipboard.ai.data.source.local.entity.TopicActionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicActionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TopicActionEntity): Long

    @Update
    suspend fun update(entity: TopicActionEntity)

    @Query(
        """
        SELECT * FROM topic_actions
        WHERE topicId = :topicId
        ORDER BY createdAtMillis ASC, id ASC
        """
    )
    fun observeByTopicId(topicId: Long): Flow<List<TopicActionEntity>>
}
