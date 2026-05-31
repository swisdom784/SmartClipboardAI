package com.smartclipboard.ai.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.smartclipboard.ai.data.source.local.entity.DataItemEntity
import com.smartclipboard.ai.data.source.local.entity.TopicEntity
import com.smartclipboard.ai.data.source.local.entity.TopicItemCrossRefEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(entity: TopicEntity): Long

    @Update
    suspend fun updateTopic(entity: TopicEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopicItemCrossRefs(entities: List<TopicItemCrossRefEntity>)

    @Query("DELETE FROM topic_item_cross_refs WHERE topicId = :topicId")
    suspend fun deleteTopicItemCrossRefs(topicId: Long): Int

    @Transaction
    suspend fun replaceTopicItemCrossRefs(
        topicId: Long,
        entities: List<TopicItemCrossRefEntity>
    ) {
        deleteTopicItemCrossRefs(topicId)
        if (entities.isNotEmpty()) {
            insertTopicItemCrossRefs(entities)
        }
    }

    @Query("SELECT * FROM topics WHERE id = :id")
    suspend fun getTopicById(id: Long): TopicEntity?

    @Query("SELECT * FROM topics ORDER BY updatedAtMillis DESC, id DESC")
    fun observeTopics(): Flow<List<TopicEntity>>

    @Transaction
    @Query(
        """
        SELECT data_items.* FROM data_items
        INNER JOIN topic_item_cross_refs ON data_items.id = topic_item_cross_refs.dataItemId
        WHERE topic_item_cross_refs.topicId = :topicId AND data_items.deletedAtMillis IS NULL
        ORDER BY topic_item_cross_refs.createdAtMillis ASC
        """
    )
    fun observeDataItemsForTopic(topicId: Long): Flow<List<DataItemEntity>>

    @Query("SELECT DISTINCT dataItemId FROM topic_item_cross_refs")
    suspend fun getLinkedDataItemIds(): List<Long>
}
