package com.smartclipboard.ai.presentation.topic.selection

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.Topic
import com.smartclipboard.ai.domain.model.TopicAction
import com.smartclipboard.ai.domain.model.TopicAnalysis
import com.smartclipboard.ai.domain.model.TopicItemSelectedBy
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
import org.junit.Assert.assertNull
import org.junit.Test

class TopicDataSelectionUseCaseTest {
    @Test
    fun summarizesSelectedItemsByCountAndType() {
        val useCase = TopicDataSelectionUseCase(RecordingTopicSelectionRepository())

        val summary = useCase.summarize(
            listOf(
                dataItem(id = 1L, type = DataItemType.IMAGE),
                dataItem(id = 2L, type = DataItemType.SCREENSHOT),
                dataItem(id = 3L, type = DataItemType.LINK),
                dataItem(id = 4L, type = DataItemType.TEXT)
            )
        )

        assertEquals("사용된 자료 4개", summary.title)
        assertEquals("이미지 2 · 링크 1 · 텍스트 1", summary.subtitle)
    }

    @Test
    fun savesDistinctIdsAsUserSelection() = runBlocking {
        val repository = RecordingTopicSelectionRepository()
        val useCase = TopicDataSelectionUseCase(repository)

        val result = useCase.saveUserSelection(
            topicId = 12L,
            selectedDataItemIds = listOf(5L, 7L, 5L)
        )

        assertEquals(TopicDataSelectionSaveResult.Saved(selectedCount = 2), result)
        assertEquals(12L, repository.replacedTopicId)
        assertEquals(listOf(5L, 7L), repository.replacedDataItemIds)
        assertEquals(TopicItemSelectedBy.USER, repository.replacedSelectedBy)
    }

    @Test
    fun ignoresSaveWhenTopicIdIsInvalid() = runBlocking {
        val repository = RecordingTopicSelectionRepository()
        val useCase = TopicDataSelectionUseCase(repository)

        val result = useCase.saveUserSelection(
            topicId = 0L,
            selectedDataItemIds = listOf(5L)
        )

        assertEquals(TopicDataSelectionSaveResult.Ignored, result)
        assertNull(repository.replacedTopicId)
    }

    private class RecordingTopicSelectionRepository : DataRepository {
        var replacedTopicId: Long? = null
        var replacedDataItemIds: List<Long> = emptyList()
        var replacedSelectedBy: TopicItemSelectedBy? = null

        override suspend fun saveDataItem(item: DataItem): Long = error("not used")
        override suspend fun getDataItem(id: Long): DataItem? = null
        override fun observeDataItems(): Flow<List<DataItem>> = emptyFlow()
        override fun observeDataItemsByType(types: Set<DataItemType>): Flow<List<DataItem>> = emptyFlow()
        override suspend fun createTopic(
            topic: Topic,
            dataItemIds: List<Long>,
            selectedBy: TopicItemSelectedBy
        ): Long = error("not used")
        override fun observeTopics(): Flow<List<Topic>> = emptyFlow()
        override fun observeDataItemsForTopic(topicId: Long): Flow<List<DataItem>> = emptyFlow()
        override suspend fun replaceTopicDataItems(
            topicId: Long,
            dataItemIds: List<Long>,
            selectedBy: TopicItemSelectedBy
        ) {
            replacedTopicId = topicId
            replacedDataItemIds = dataItemIds
            replacedSelectedBy = selectedBy
        }
        override suspend fun saveTopicAnalysis(analysis: TopicAnalysis): Long = error("not used")
        override fun observeTopicAnalyses(topicId: Long): Flow<List<TopicAnalysis>> = emptyFlow()
        override suspend fun saveTopicAction(action: TopicAction): Long = error("not used")
        override suspend fun updateTopicAction(action: TopicAction) = Unit
        override fun observeTopicActions(topicId: Long): Flow<List<TopicAction>> = emptyFlow()
        override fun observeHomeState(): Flow<HomeRepositoryState> = emptyFlow()
        override fun observeInboxItems(filter: InboxFilter): Flow<List<DataItem>> = emptyFlow()
        override fun observeCurrentRecommendationSession(): Flow<RecommendationSession?> = emptyFlow()
        override suspend fun getStorageUsage(quotaBytes: Long): StorageUsageSummary = error("not used")
        override suspend fun cleanupStorage(quotaBytes: Long): StorageCleanupResult = error("not used")
    }

    private fun dataItem(
        id: Long,
        type: DataItemType
    ): DataItem {
        return DataItem(
            id = id,
            type = type,
            source = DataItemSource.MANUAL,
            textContent = "item $id",
            capturedAtMillis = id,
            createdAtMillis = id,
            updatedAtMillis = id
        )
    }
}
