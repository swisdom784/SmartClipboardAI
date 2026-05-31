package com.smartclipboard.ai.data.repository

import com.smartclipboard.ai.data.source.local.dao.DataItemDao
import com.smartclipboard.ai.data.source.local.dao.TopicActionDao
import com.smartclipboard.ai.data.source.local.dao.TopicAnalysisDao
import com.smartclipboard.ai.data.source.local.dao.TopicDao
import com.smartclipboard.ai.data.source.local.entity.DataItemEntity
import com.smartclipboard.ai.data.source.local.entity.TopicActionEntity
import com.smartclipboard.ai.data.source.local.entity.TopicAnalysisEntity
import com.smartclipboard.ai.data.source.local.entity.TopicEntity
import com.smartclipboard.ai.data.source.local.entity.TopicItemCrossRefEntity
import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemStorage
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.EnrichmentStatus
import com.smartclipboard.ai.domain.model.Topic
import com.smartclipboard.ai.domain.model.TopicOrigin
import com.smartclipboard.ai.domain.repository.InboxFilter
import com.smartclipboard.ai.processing.gemini.recommendation.GeminiTopicRecommendationManager
import com.smartclipboard.ai.processing.gemini.recommendation.InMemoryRecommendationSessionStore
import com.smartclipboard.ai.processing.gemini.recommendation.RecommendationDataSource
import com.smartclipboard.ai.processing.gemini.recommendation.RecommendationSessionStatus
import com.smartclipboard.ai.processing.gemini.recommendation.TopicRecommendationCandidate
import com.smartclipboard.ai.processing.gemini.recommendation.TopicRecommendationGenerator
import com.smartclipboard.ai.storage.StorageCleanupManager
import com.smartclipboard.ai.storage.StorageCleanupStore
import com.smartclipboard.ai.storage.StorageQuotaPolicy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class DataRepositoryImplIntegrationTest {
    @Test
    fun `home state combines recent data items topics and current recommendation session`() = runBlocking {
        val dataItemDao = FakeDataItemDao(
            listOf(
                dataEntity(id = 1L, capturedAtMillis = 10L),
                dataEntity(id = 2L, capturedAtMillis = 20L)
            )
        )
        val topicDao = FakeTopicDao(
            topics = listOf(topicEntity(id = 10L, title = "여행 준비"))
        )
        val sessionStore = InMemoryRecommendationSessionStore()
        val repository = repository(
            dataItemDao = dataItemDao,
            topicDao = topicDao,
            sessionStore = sessionStore
        )
        sessionStore.replaceCurrentSession(
            com.smartclipboard.ai.processing.gemini.recommendation.RecommendationSession(
                id = "session",
                status = RecommendationSessionStatus.READY,
                recommendations = listOf(
                    TopicRecommendationCandidate(
                        id = "rec",
                        title = "최근 자료 정리",
                        reason = "새 자료가 있어요",
                        prompt = "최근 자료를 정리해줘",
                        sourceDataItemIds = listOf(1L),
                        createdAtMillis = 100L
                    )
                ),
                createdAtMillis = 100L
            )
        )

        val state = repository.observeHomeState().first()

        assertEquals(listOf(2L, 1L), state.recentDataItems.map { it.id })
        assertEquals(listOf("여행 준비"), state.activeTopics.map { it.title })
        assertEquals("rec", state.recommendationSession?.recommendations?.single()?.id)
    }

    @Test
    fun `inbox items can be filtered by type importance and pending analysis`() = runBlocking {
        val repository = repository(
            dataItemDao = FakeDataItemDao(
                listOf(
                    dataEntity(
                        id = 1L,
                        type = DataItemType.IMAGE,
                        isImportant = true,
                        enrichmentStatus = EnrichmentStatus.PENDING
                    ),
                    dataEntity(
                        id = 2L,
                        type = DataItemType.IMAGE,
                        isImportant = false,
                        enrichmentStatus = EnrichmentStatus.DONE
                    ),
                    dataEntity(
                        id = 3L,
                        type = DataItemType.LINK,
                        isImportant = true,
                        enrichmentStatus = EnrichmentStatus.PENDING
                    )
                )
            )
        )

        val items = repository.observeInboxItems(
            InboxFilter(
                types = setOf(DataItemType.IMAGE),
                importantOnly = true,
                pendingAnalysisOnly = true
            )
        ).first()

        assertEquals(listOf(1L), items.map { it.id })
    }

    @Test
    fun `recommendation refresh and storage usage are exposed through repository`() = runBlocking {
        val sessionStore = InMemoryRecommendationSessionStore()
        val repository = repository(
            dataItemDao = FakeDataItemDao(
                listOf(dataEntity(id = 1L, sizeBytes = 700L))
            ),
            sessionStore = sessionStore,
            recommendationGenerator = FakeRecommendationGenerator()
        )

        val session = repository.refreshTopicRecommendations(limit = 10)
        val usage = repository.getStorageUsage(quotaBytes = 1_000L)

        assertEquals(RecommendationSessionStatus.READY, session.status)
        assertEquals("rec", sessionStore.currentSession.value?.recommendations?.single()?.id)
        assertEquals(700L, usage.usedBytes)
        assertEquals(0L, usage.overQuotaBytes)
    }

    private fun repository(
        dataItemDao: FakeDataItemDao = FakeDataItemDao(emptyList()),
        topicDao: FakeTopicDao = FakeTopicDao(emptyList()),
        sessionStore: InMemoryRecommendationSessionStore = InMemoryRecommendationSessionStore(),
        recommendationGenerator: TopicRecommendationGenerator = FakeRecommendationGenerator()
    ): DataRepositoryImpl {
        val recommendationManager = GeminiTopicRecommendationManager(
            dataSource = FakeRecommendationDataSource(dataItemDao.domainItems()),
            generator = recommendationGenerator,
            sessionStore = sessionStore,
            nowMillis = { 1_000L }
        )
        val storageCleanupManager = StorageCleanupManager(
            store = FakeStorageCleanupStore(dataItemDao.domainItems()),
            policy = StorageQuotaPolicy(),
            nowMillis = { 1_000L }
        )

        return DataRepositoryImpl(
            dataItemDao = dataItemDao,
            topicDao = topicDao,
            topicAnalysisDao = FakeTopicAnalysisDao(),
            topicActionDao = FakeTopicActionDao(),
            recommendationManager = recommendationManager,
            recommendationSessionStore = sessionStore,
            storageCleanupManager = storageCleanupManager
        )
    }

    private fun dataEntity(
        id: Long,
        type: DataItemType = DataItemType.TEXT,
        capturedAtMillis: Long = 1L,
        sizeBytes: Long? = null,
        isImportant: Boolean = false,
        enrichmentStatus: EnrichmentStatus = EnrichmentStatus.DONE
    ): DataItemEntity {
        return DataItemEntity(
            id = id,
            type = type.name,
            source = DataItemSource.MANUAL.name,
            textContent = "item $id",
            sourceUri = "content://item/$id",
            capturedAtMillis = capturedAtMillis,
            createdAtMillis = capturedAtMillis,
            updatedAtMillis = capturedAtMillis,
            sizeBytes = sizeBytes,
            enrichmentStatus = enrichmentStatus.name,
            isImportant = isImportant
        )
    }

    private fun topicEntity(
        id: Long,
        title: String
    ): TopicEntity {
        return TopicEntity(
            id = id,
            title = title,
            origin = TopicOrigin.USER_REQUEST.name,
            createdAtMillis = 1L,
            updatedAtMillis = 1L
        )
    }

    private class FakeDataItemDao(
        initialItems: List<DataItemEntity>
    ) : DataItemDao {
        private val items = MutableStateFlow(initialItems)

        fun domainItems(): List<DataItem> {
            return items.value.map {
                DataItem(
                    id = it.id,
                    type = DataItemType.valueOf(it.type),
                    source = DataItemSource.valueOf(it.source),
                    textContent = it.textContent,
                    sourceUri = it.sourceUri,
                    capturedAtMillis = it.capturedAtMillis,
                    createdAtMillis = it.createdAtMillis,
                    updatedAtMillis = it.updatedAtMillis,
                    sizeBytes = it.sizeBytes,
                    storage = DataItemStorage(isImportant = it.isImportant)
                )
            }
        }

        override suspend fun insert(entity: DataItemEntity): Long = entity.id

        override suspend fun update(entity: DataItemEntity) = Unit

        override suspend fun getById(id: Long): DataItemEntity? = items.value.firstOrNull { it.id == id }

        override fun observeAll(): Flow<List<DataItemEntity>> = items

        override fun observeByTypes(types: List<String>): Flow<List<DataItemEntity>> {
            return MutableStateFlow(items.value.filter { it.type in types })
        }

        override suspend fun getPendingForEnrichment(
            types: List<String>,
            maxRetries: Int,
            limit: Int
        ): List<DataItemEntity> = emptyList()

        override suspend fun getActiveItemsForStorageCleanup(): List<DataItemEntity> = items.value

        override suspend fun softDeleteByIds(
            ids: List<Long>,
            deletedAtMillis: Long
        ): Int = ids.size
    }

    private class FakeTopicDao(
        private val topics: List<TopicEntity>
    ) : TopicDao {
        override suspend fun insertTopic(entity: TopicEntity): Long = entity.id

        override suspend fun updateTopic(entity: TopicEntity) = Unit

        override suspend fun insertTopicItemCrossRefs(entities: List<TopicItemCrossRefEntity>) = Unit

        override suspend fun getTopicById(id: Long): TopicEntity? = topics.firstOrNull { it.id == id }

        override fun observeTopics(): Flow<List<TopicEntity>> = MutableStateFlow(topics)

        override fun observeDataItemsForTopic(topicId: Long): Flow<List<DataItemEntity>> = MutableStateFlow(emptyList())

        override suspend fun getLinkedDataItemIds(): List<Long> = emptyList()
    }

    private class FakeTopicAnalysisDao : TopicAnalysisDao {
        override suspend fun insert(entity: TopicAnalysisEntity): Long = entity.id

        override suspend fun update(entity: TopicAnalysisEntity) = Unit

        override fun observeByTopicId(topicId: Long): Flow<List<TopicAnalysisEntity>> = MutableStateFlow(emptyList())
    }

    private class FakeTopicActionDao : TopicActionDao {
        override suspend fun insert(entity: TopicActionEntity): Long = entity.id

        override suspend fun update(entity: TopicActionEntity) = Unit

        override fun observeByTopicId(topicId: Long): Flow<List<TopicActionEntity>> = MutableStateFlow(emptyList())
    }

    private class FakeRecommendationDataSource(
        private val items: List<DataItem>
    ) : RecommendationDataSource {
        override suspend fun getRecommendationInputItems(limit: Int): List<DataItem> = items.take(limit)
    }

    private class FakeRecommendationGenerator : TopicRecommendationGenerator {
        override suspend fun generate(items: List<DataItem>): List<TopicRecommendationCandidate> {
            return listOf(
                TopicRecommendationCandidate(
                    id = "rec",
                    title = "최근 자료 정리",
                    reason = "새 자료가 있어요",
                    prompt = "최근 자료를 정리해줘",
                    sourceDataItemIds = items.map { it.id },
                    createdAtMillis = 1_000L
                )
            )
        }
    }

    private class FakeStorageCleanupStore(
        private val items: List<DataItem>
    ) : StorageCleanupStore {
        override suspend fun getActiveItems(): List<DataItem> = items

        override suspend fun getTopicLinkedDataItemIds(): Set<Long> = emptySet()

        override suspend fun softDeleteDataItems(
            itemIds: List<Long>,
            deletedAtMillis: Long
        ): Int = itemIds.size
    }
}
