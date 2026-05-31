package com.smartclipboard.ai.domain.repository

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.Topic
import com.smartclipboard.ai.domain.model.TopicAction
import com.smartclipboard.ai.domain.model.TopicAnalysis
import com.smartclipboard.ai.domain.model.TopicItemSelectedBy
import com.smartclipboard.ai.processing.gemini.recommendation.RecommendationSession
import com.smartclipboard.ai.storage.StorageCleanupResult
import com.smartclipboard.ai.storage.StorageUsageSummary
import kotlinx.coroutines.flow.emptyFlow
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

    suspend fun replaceTopicDataItems(
        topicId: Long,
        dataItemIds: List<Long>,
        selectedBy: TopicItemSelectedBy = TopicItemSelectedBy.USER
    ) {
        error("Topic data item replacement is not implemented")
    }

    suspend fun saveTopicAnalysis(analysis: TopicAnalysis): Long

    fun observeTopicAnalyses(topicId: Long): Flow<List<TopicAnalysis>>

    suspend fun saveTopicAction(action: TopicAction): Long

    suspend fun updateTopicAction(action: TopicAction)

    fun observeTopicActions(topicId: Long): Flow<List<TopicAction>>

    fun observeHomeState(): Flow<HomeRepositoryState> = emptyFlow()

    fun observeInboxItems(filter: InboxFilter = InboxFilter()): Flow<List<DataItem>> = emptyFlow()

    fun observeCurrentRecommendationSession(): Flow<RecommendationSession?> = emptyFlow()

    suspend fun refreshTopicRecommendations(limit: Int = 20): RecommendationSession {
        error("Recommendation refresh is not implemented")
    }

    suspend fun getStorageUsage(quotaBytes: Long): StorageUsageSummary {
        error("Storage usage is not implemented")
    }

    suspend fun cleanupStorage(quotaBytes: Long): StorageCleanupResult {
        error("Storage cleanup is not implemented")
    }
}
