package com.smartclipboard.ai.domain.repository

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.Topic
import com.smartclipboard.ai.domain.model.TopicAction
import com.smartclipboard.ai.domain.model.TopicAnalysis
import com.smartclipboard.ai.domain.model.TopicItemSelectedBy
import kotlinx.coroutines.flow.Flow

interface DataRepository {
    suspend fun saveDataItem(item: DataItem): Long

    suspend fun getDataItem(id: Long): DataItem?

    fun observeDataItems(): Flow<List<DataItem>>

    fun observeDataItemsByType(types: Set<DataItemType>): Flow<List<DataItem>>

    suspend fun createTopic(
        topic: Topic,
        dataItemIds: List<Long> = emptyList(),
        selectedBy: TopicItemSelectedBy = TopicItemSelectedBy.USER
    ): Long

    fun observeTopics(): Flow<List<Topic>>

    fun observeDataItemsForTopic(topicId: Long): Flow<List<DataItem>>

    suspend fun saveTopicAnalysis(analysis: TopicAnalysis): Long

    fun observeTopicAnalyses(topicId: Long): Flow<List<TopicAnalysis>>

    suspend fun saveTopicAction(action: TopicAction): Long

    suspend fun updateTopicAction(action: TopicAction)

    fun observeTopicActions(topicId: Long): Flow<List<TopicAction>>
}
