package com.smartclipboard.ai.presentation.analysis

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.Topic
import com.smartclipboard.ai.domain.model.TopicAction
import com.smartclipboard.ai.domain.model.TopicAnalysis
import com.smartclipboard.ai.domain.model.TopicAnalysisStatus
import com.smartclipboard.ai.domain.model.TopicItemSelectedBy
import com.smartclipboard.ai.domain.model.TopicOrigin
import com.smartclipboard.ai.domain.repository.DataRepository
import com.smartclipboard.ai.processing.gemini.analysis.TopicAnalysisDraft
import com.smartclipboard.ai.processing.gemini.analysis.TopicAnalysisGenerator
import com.smartclipboard.ai.processing.gemini.analysis.TopicAnalysisInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TopicAnalysisUseCaseTest {
    @Test
    fun generatesDoneAnalysisFromSelectedTopicAndItems() = runBlocking {
        val repository = RecordingAnalysisRepository(
            topics = listOf(topic(id = 9L)),
            selectedItems = listOf(dataItem(id = 1L), dataItem(id = 2L))
        )
        val generator = RecordingTopicAnalysisGenerator(
            result = TopicAnalysisDraft(
                summary = "출장 준비 자료를 정리했습니다.",
                evidence = listOf("dataItemId=1: 항공권", "dataItemId=2: 숙소"),
                modelName = "gemini-2.5-flash"
            )
        )
        val useCase = TopicAnalysisUseCase(
            repository = repository,
            generator = generator,
            nowMillis = { 1_000L }
        )

        val result = useCase.generate(topicId = 9L)

        assertEquals(TopicAnalysisGenerationResult.Generated(analysisId = 100L), result)
        assertEquals(9L, generator.input?.topic?.id)
        assertEquals(listOf(1L, 2L), generator.input?.selectedItems?.map { it.id })
        assertEquals(listOf(TopicAnalysisStatus.RUNNING, TopicAnalysisStatus.DONE), repository.saved.map { it.status })
        val done = repository.saved.last()
        assertEquals(100L, done.id)
        assertEquals("출장 준비 자료를 정리했습니다.", done.summary)
        assertEquals(listOf("dataItemId=1: 항공권", "dataItemId=2: 숙소"), done.evidence)
        assertEquals("gemini-2.5-flash", done.modelName)
    }

    @Test
    fun savesFailedAnalysisWhenGeneratorFails() = runBlocking {
        val repository = RecordingAnalysisRepository(
            topics = listOf(topic(id = 9L)),
            selectedItems = listOf(dataItem(id = 1L))
        )
        val useCase = TopicAnalysisUseCase(
            repository = repository,
            generator = ThrowingTopicAnalysisGenerator(),
            nowMillis = { 2_000L }
        )

        val result = useCase.generate(topicId = 9L)

        assertTrue(result is TopicAnalysisGenerationResult.Failed)
        assertEquals(listOf(TopicAnalysisStatus.RUNNING, TopicAnalysisStatus.FAILED), repository.saved.map { it.status })
        val failed = repository.saved.last()
        assertEquals(1, failed.retryCount)
        assertEquals("analysis failed", failed.failureReason)
    }

    private class RecordingAnalysisRepository(
        private val topics: List<Topic>,
        private val selectedItems: List<DataItem>
    ) : DataRepository {
        val saved = mutableListOf<TopicAnalysis>()

        override suspend fun saveDataItem(item: DataItem): Long = error("not used")
        override suspend fun getDataItem(id: Long): DataItem? = null
        override fun observeDataItems(): Flow<List<DataItem>> = emptyFlow()
        override fun observeDataItemsByType(types: Set<DataItemType>): Flow<List<DataItem>> = emptyFlow()
        override suspend fun createTopic(
            topic: Topic,
            dataItemIds: List<Long>,
            selectedBy: TopicItemSelectedBy
        ): Long = error("not used")
        override fun observeTopics(): Flow<List<Topic>> = flowOf(topics)
        override fun observeDataItemsForTopic(topicId: Long): Flow<List<DataItem>> = flowOf(selectedItems)
        override suspend fun saveTopicAnalysis(analysis: TopicAnalysis): Long {
            val id = if (analysis.id == 0L) 100L + saved.count { it.id == 0L } else analysis.id
            saved += analysis.copy(id = id)
            return id
        }
        override fun observeTopicAnalyses(topicId: Long): Flow<List<TopicAnalysis>> = flowOf(saved)
        override suspend fun saveTopicAction(action: TopicAction): Long = error("not used")
        override suspend fun updateTopicAction(action: TopicAction) = Unit
        override fun observeTopicActions(topicId: Long): Flow<List<TopicAction>> = emptyFlow()
    }

    private class RecordingTopicAnalysisGenerator(
        private val result: TopicAnalysisDraft
    ) : TopicAnalysisGenerator {
        var input: TopicAnalysisInput? = null

        override suspend fun generate(input: TopicAnalysisInput): TopicAnalysisDraft {
            this.input = input
            return result
        }
    }

    private class ThrowingTopicAnalysisGenerator : TopicAnalysisGenerator {
        override suspend fun generate(input: TopicAnalysisInput): TopicAnalysisDraft {
            error("analysis failed")
        }
    }

    private fun topic(id: Long): Topic {
        return Topic(
            id = id,
            title = "출장 준비",
            prompt = "출장 자료 정리해줘",
            origin = TopicOrigin.USER_REQUEST,
            createdAtMillis = 1L,
            updatedAtMillis = 1L
        )
    }

    private fun dataItem(id: Long): DataItem {
        return DataItem(
            id = id,
            type = DataItemType.TEXT,
            source = DataItemSource.MANUAL,
            textContent = "item $id",
            capturedAtMillis = id,
            createdAtMillis = id,
            updatedAtMillis = id
        )
    }
}
