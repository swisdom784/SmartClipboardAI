package com.smartclipboard.ai.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.smartclipboard.ai.data.source.local.entity.TopicAnalysisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicAnalysisDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TopicAnalysisEntity): Long

    @Update
    suspend fun update(entity: TopicAnalysisEntity)

    @Query(
        """
        SELECT * FROM topic_analyses
        WHERE topicId = :topicId
        ORDER BY createdAtMillis DESC, id DESC
        """
    )
    fun observeByTopicId(topicId: Long): Flow<List<TopicAnalysisEntity>>
}
