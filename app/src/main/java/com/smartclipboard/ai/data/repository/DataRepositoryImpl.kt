package com.smartclipboard.ai.data.repository

import com.smartclipboard.ai.data.source.local.dao.DataItemDao
import com.smartclipboard.ai.data.source.local.dao.TopicActionDao
import com.smartclipboard.ai.data.source.local.dao.TopicAnalysisDao
import com.smartclipboard.ai.data.source.local.dao.TopicDao
import com.smartclipboard.ai.data.source.local.entity.TopicItemCrossRefEntity
import com.smartclipboard.ai.data.source.local.mapper.toDomain
import com.smartclipboard.ai.data.source.local.mapper.toEntity
import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.Topic
import com.smartclipboard.ai.domain.model.TopicAction
import com.smartclipboard.ai.domain.model.TopicStatus
import com.smartclipboard.ai.domain.model.TopicAnalysis
import com.smartclipboard.ai.domain.model.TopicItemSelectedBy
import com.smartclipboard.ai.domain.repository.DataRepository
import com.smartclipboard.ai.domain.repository.HomeRepositoryState
import com.smartclipboard.ai.domain.repository.InboxFilter
import com.smartclipboard.ai.domain.model.EnrichmentStatus
import com.smartclipboard.ai.processing.gemini.recommendation.GeminiTopicRecommendationManager
import com.smartclipboard.ai.processing.gemini.recommendation.RecommendationSession
import com.smartclipboard.ai.processing.gemini.recommendation.RecommendationSessionStore
import com.smartclipboard.ai.storage.StorageCleanupManager
import com.smartclipboard.ai.storage.StorageCleanupResult
import com.smartclipboard.ai.storage.StorageUsageSummary
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class DataRepositoryImpl @Inject constructor(
    private val dataItemDao: DataItemDao,
    private val topicDao: TopicDao,
    private val topicAnalysisDao: TopicAnalysisDao,
    private val topicActionDao: TopicActionDao,
    private val recommendationManager: GeminiTopicRecommendationManager,
    private val recommendationSessionStore: RecommendationSessionStore,
    private val storageCleanupManager: StorageCleanupManager
) : DataRepository {
    override suspend fun saveDataItem(item: DataItem): Long {
        return dataItemDao.insert(item.toEntity())
    }

    override suspend fun getDataItem(id: Long): DataItem? {
        return dataItemDao.getById(id)?.toDomain()
    }

    override fun observeDataItems(): Flow<List<DataItem>> {
        return dataItemDao.observeAll().map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeDataItemsByType(types: Set<DataItemType>): Flow<List<DataItem>> {
        val typeNames = types.map { it.name }
        return dataItemDao.observeByTypes(typeNames).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun createTopic(
        topic: Topic,
        dataItemIds: List<Long>,
        selectedBy: TopicItemSelectedBy
    ): Long {
        val topicId = topicDao.insertTopic(topic.toEntity())
        if (dataItemIds.isNotEmpty()) {
            topicDao.insertTopicItemCrossRefs(
                dataItemIds.map { dataItemId ->
                    TopicItemCrossRefEntity(
                        topicId = topicId,
                        dataItemId = dataItemId,
                        selectedBy = selectedBy.name,
                        createdAtMillis = topic.createdAtMillis
                    )
                }
            )
        }
        return topicId
    }

    override fun observeTopics(): Flow<List<Topic>> {
        return topicDao.observeTopics().map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeDataItemsForTopic(topicId: Long): Flow<List<DataItem>> {
        return topicDao.observeDataItemsForTopic(topicId)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun replaceTopicDataItems(
        topicId: Long,
        dataItemIds: List<Long>,
        selectedBy: TopicItemSelectedBy
    ) {
        val now = System.currentTimeMillis()
        topicDao.replaceTopicItemCrossRefs(
            topicId = topicId,
            entities = dataItemIds.distinct().map { dataItemId ->
                TopicItemCrossRefEntity(
                    topicId = topicId,
                    dataItemId = dataItemId,
                    selectedBy = selectedBy.name,
                    createdAtMillis = now
                )
            }
        )
    }

    override suspend fun saveTopicAnalysis(analysis: TopicAnalysis): Long {
        return topicAnalysisDao.insert(analysis.toEntity())
    }

    override fun observeTopicAnalyses(topicId: Long): Flow<List<TopicAnalysis>> {
        return topicAnalysisDao.observeByTopicId(topicId)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun saveTopicAction(action: TopicAction): Long {
        return topicActionDao.insert(action.toEntity())
    }

    override suspend fun updateTopicAction(action: TopicAction) {
        topicActionDao.update(action.toEntity())
    }

    override fun observeTopicActions(topicId: Long): Flow<List<TopicAction>> {
        return topicActionDao.observeByTopicId(topicId)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeHomeState(): Flow<HomeRepositoryState> {
        return combine(
            observeDataItems(),
            observeTopics(),
            recommendationSessionStore.currentSession
        ) { dataItems, topics, recommendationSession ->
            HomeRepositoryState(
                recentDataItems = dataItems
                    .sortedWith(compareByDescending<DataItem> { it.capturedAtMillis }.thenByDescending { it.id })
                    .take(HOME_RECENT_ITEM_LIMIT),
                activeTopics = topics
                    .filterNot { it.status == TopicStatus.ARCHIVED }
                    .sortedWith(compareByDescending<Topic> { it.updatedAtMillis }.thenByDescending { it.id }),
                recommendationSession = recommendationSession
            )
        }
    }

    override fun observeInboxItems(filter: InboxFilter): Flow<List<DataItem>> {
        val source = if (filter.types.isEmpty()) {
            observeDataItems()
        } else {
            observeDataItemsByType(filter.types)
        }

        return source.map { items ->
            items.filter { item ->
                (!filter.importantOnly || item.storage.isImportant) &&
                    (!filter.pendingAnalysisOnly || item.enrichment.status.isPendingForUser())
            }
        }
    }

    override fun observeCurrentRecommendationSession(): Flow<RecommendationSession?> {
        return recommendationSessionStore.currentSession
    }

    override suspend fun refreshTopicRecommendations(limit: Int): RecommendationSession {
        return recommendationManager.refresh(limit)
    }

    override suspend fun getStorageUsage(quotaBytes: Long): StorageUsageSummary {
        return storageCleanupManager.calculateUsage(quotaBytes)
    }

    override suspend fun cleanupStorage(quotaBytes: Long): StorageCleanupResult {
        return storageCleanupManager.cleanup(quotaBytes)
    }

    private fun EnrichmentStatus.isPendingForUser(): Boolean {
        return this == EnrichmentStatus.PENDING || this == EnrichmentStatus.PROCESSING
    }

    private companion object {
        const val HOME_RECENT_ITEM_LIMIT = 12
    }
}
