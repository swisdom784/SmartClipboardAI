package com.smartclipboard.ai.presentation.topic

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.Topic
import com.smartclipboard.ai.domain.model.TopicItemSelectedBy
import com.smartclipboard.ai.domain.model.TopicOrigin
import com.smartclipboard.ai.domain.repository.DataRepository
import com.smartclipboard.ai.domain.repository.HomeRepositoryState
import com.smartclipboard.ai.domain.repository.InboxFilter
import com.smartclipboard.ai.processing.gemini.recommendation.RecommendationSession
import com.smartclipboard.ai.storage.StorageCleanupResult
import com.smartclipboard.ai.storage.StorageUsageSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TopicCreateUseCaseTest {
    @Test
    fun createsTopicFromUserRequest() = runBlocking {
        val repository = RecordingTopicRepository()
        val useCase = TopicCreateUseCase(
            repository = repository,
            nowMillis = { 1_000L }
        )

        val result = useCase.createFromUserRequest("  제주 여행 일정 정리해줘  ")

        assertEquals(TopicCreateResult.Created(topicId = 42L), result)
        assertEquals("제주 여행 일정 정리해줘", repository.createdTopic?.title)
        assertEquals("제주 여행 일정 정리해줘", repository.createdTopic?.prompt)
        assertEquals(TopicOrigin.USER_REQUEST, repository.createdTopic?.origin)
        assertEquals(emptyList<Long>(), repository.dataItemIds)
        assertEquals(TopicItemSelectedBy.USER, repository.selectedBy)
    }

    @Test
    fun createsTopicFromAiRecommendationWithSourceItems() = runBlocking {
        val repository = RecordingTopicRepository()
        val useCase = TopicCreateUseCase(
            repository = repository,
            nowMillis = { 2_000L }
        )

        val result = useCase.createFromRecommendation(
            TopicRecommendationSelection(
                title = "최근 자료 정리",
                prompt = "최근 자료를 정리해줘",
                sourceDataItemIds = listOf(7L, 8L)
            )
        )

        assertEquals(TopicCreateResult.Created(topicId = 42L), result)
        assertEquals("최근 자료 정리", repository.createdTopic?.title)
        assertEquals("최근 자료를 정리해줘", repository.createdTopic?.prompt)
        assertEquals(TopicOrigin.AI_RECOMMENDATION, repository.createdTopic?.origin)
        assertEquals(listOf(7L, 8L), repository.dataItemIds)
        assertEquals(TopicItemSelectedBy.AI, repository.selectedBy)
    }

    @Test
    fun ignoresBlankUserRequest() = runBlocking {
        val repository = RecordingTopicRepository()
        val useCase = TopicCreateUseCase(
            repository = repository,
            nowMillis = { 1_000L }
        )

        val result = useCase.createFromUserRequest("   ")

        assertEquals(TopicCreateResult.Ignored, result)
        assertTrue(repository.createdTopic == null)
    }

    private class RecordingTopicRepository : DataRepository {
        var createdTopic: Topic? = null
        var dataItemIds: List<Long> = emptyList()
        var selectedBy: TopicItemSelectedBy? = null

        override suspend fun saveDataItem(item: DataItem): Long = error("not used")
        override suspend fun getDataItem(id: Long): DataItem? = null
        override fun observeDataItems(): Flow<List<DataItem>> = emptyFlow()
        override fun observeDataItemsByType(types: Set<DataItemType>): Flow<List<DataItem>> = emptyFlow()
        override suspend fun createTopic(
            topic: Topic,
            dataItemIds: List<Long>,
            selectedBy: TopicItemSelectedBy
        ): Long {
            createdTopic = topic
            this.dataItemIds = dataItemIds
            this.selectedBy = selectedBy
            return 42L
        }
        override fun observeTopics(): Flow<List<Topic>> = emptyFlow()
        override fun observeDataItemsForTopic(topicId: Long): Flow<List<DataItem>> = emptyFlow()
        override suspend fun saveTopicAnalysis(analysis: com.smartclipboard.ai.domain.model.TopicAnalysis): Long = error("not used")
        override fun observeTopicAnalyses(topicId: Long): Flow<List<com.smartclipboard.ai.domain.model.TopicAnalysis>> = emptyFlow()
        override suspend fun saveTopicAction(action: com.smartclipboard.ai.domain.model.TopicAction): Long = error("not used")
        override suspend fun updateTopicAction(action: com.smartclipboard.ai.domain.model.TopicAction) = Unit
        override fun observeTopicActions(topicId: Long): Flow<List<com.smartclipboard.ai.domain.model.TopicAction>> = emptyFlow()
        override fun observeHomeState(): Flow<HomeRepositoryState> = emptyFlow()
        override fun observeInboxItems(filter: InboxFilter): Flow<List<DataItem>> = emptyFlow()
        override fun observeCurrentRecommendationSession(): Flow<RecommendationSession?> = emptyFlow()
        override suspend fun getStorageUsage(quotaBytes: Long): StorageUsageSummary = error("not used")
        override suspend fun cleanupStorage(quotaBytes: Long): StorageCleanupResult = error("not used")
    }
}
