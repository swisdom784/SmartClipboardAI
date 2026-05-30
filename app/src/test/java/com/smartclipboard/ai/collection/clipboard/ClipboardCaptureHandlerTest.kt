package com.smartclipboard.ai.collection.clipboard

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.Topic
import com.smartclipboard.ai.domain.model.TopicAction
import com.smartclipboard.ai.domain.model.TopicAnalysis
import com.smartclipboard.ai.domain.model.TopicItemSelectedBy
import com.smartclipboard.ai.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClipboardCaptureHandlerTest {
    private val fixedNow = 1_717_100_000_000L

    @Test
    fun `plain clipboard text is saved as clipboard tile data item`() = runBlocking {
        val repository = FakeDataRepository()
        val handler = ClipboardCaptureHandler(repository, nowMillis = { fixedNow })

        val result = handler.save(ClipboardPayload(text = "회의 후 여행 일정 후보 정리"))

        assertEquals(ClipboardCaptureResult.Success(savedCount = 1), result)
        assertEquals(DataItemType.TEXT, repository.savedItems.single().type)
        assertEquals(DataItemSource.CLIPBOARD_TILE, repository.savedItems.single().source)
        assertEquals("회의 후 여행 일정 후보 정리", repository.savedItems.single().textContent)
        assertEquals(fixedNow, repository.savedItems.single().capturedAtMillis)
    }

    @Test
    fun `clipboard url is saved as link data item`() = runBlocking {
        val repository = FakeDataRepository()
        val handler = ClipboardCaptureHandler(repository, nowMillis = { fixedNow })

        val result = handler.save(ClipboardPayload(text = "https://example.com/product"))

        assertEquals(ClipboardCaptureResult.Success(savedCount = 1), result)
        assertEquals(DataItemType.LINK, repository.savedItems.single().type)
        assertEquals("https://example.com/product", repository.savedItems.single().textContent)
        assertEquals("https://example.com/product", repository.savedItems.single().sourceUri)
    }

    @Test
    fun `blank clipboard text returns empty failure without saving`() = runBlocking {
        val repository = FakeDataRepository()
        val handler = ClipboardCaptureHandler(repository, nowMillis = { fixedNow })

        val result = handler.save(ClipboardPayload(text = "   "))

        assertEquals(
            ClipboardCaptureResult.Failure(ClipboardFailureReason.EmptyClipboard),
            result
        )
        assertTrue(repository.savedItems.isEmpty())
    }

    @Test
    fun `unsupported clipboard content returns unsupported failure without saving`() = runBlocking {
        val repository = FakeDataRepository()
        val handler = ClipboardCaptureHandler(repository, nowMillis = { fixedNow })

        val result = handler.save(
            ClipboardPayload(
                text = null,
                hasUnsupportedContent = true
            )
        )

        assertEquals(
            ClipboardCaptureResult.Failure(ClipboardFailureReason.UnsupportedContent),
            result
        )
        assertTrue(repository.savedItems.isEmpty())
    }

    @Test
    fun `repository exception returns save failure`() = runBlocking {
        val repository = FakeDataRepository(throwOnSave = true)
        val handler = ClipboardCaptureHandler(repository, nowMillis = { fixedNow })

        val result = handler.save(ClipboardPayload(text = "저장 실패 재현"))

        assertEquals(
            ClipboardCaptureResult.Failure(ClipboardFailureReason.SaveFailed),
            result
        )
    }

    private class FakeDataRepository(
        private val throwOnSave: Boolean = false
    ) : DataRepository {
        val savedItems = mutableListOf<DataItem>()

        override suspend fun saveDataItem(item: DataItem): Long {
            if (throwOnSave) {
                error("Save failed")
            }
            savedItems += item
            return savedItems.size.toLong()
        }

        override suspend fun getDataItem(id: Long): DataItem? = null

        override fun observeDataItems(): Flow<List<DataItem>> = emptyFlow()

        override fun observeDataItemsByType(types: Set<DataItemType>): Flow<List<DataItem>> = emptyFlow()

        override suspend fun createTopic(
            topic: Topic,
            dataItemIds: List<Long>,
            selectedBy: TopicItemSelectedBy
        ): Long = error("Not used in this test")

        override fun observeTopics(): Flow<List<Topic>> = emptyFlow()

        override fun observeDataItemsForTopic(topicId: Long): Flow<List<DataItem>> = emptyFlow()

        override suspend fun saveTopicAnalysis(analysis: TopicAnalysis): Long = error("Not used in this test")

        override fun observeTopicAnalyses(topicId: Long): Flow<List<TopicAnalysis>> = emptyFlow()

        override suspend fun saveTopicAction(action: TopicAction): Long = error("Not used in this test")

        override suspend fun updateTopicAction(action: TopicAction) = error("Not used in this test")

        override fun observeTopicActions(topicId: Long): Flow<List<TopicAction>> = emptyFlow()
    }
}
