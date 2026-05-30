package com.smartclipboard.ai.collection.share

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

class ShareContentHandlerTest {
    private val fixedNow = 1_717_000_000_000L

    @Test
    fun `plain text share is saved as text data item`() = runBlocking {
        val repository = FakeDataRepository()
        val handler = ShareContentHandler(repository, nowMillis = { fixedNow })

        val result = handler.save(
            SharePayload(
                action = ShareAction.Send,
                mimeType = "text/plain",
                text = "회의 끝나고 구매 후보 다시 비교하기"
            )
        )

        assertEquals(ShareSaveResult.Success(savedCount = 1), result)
        assertEquals(DataItemType.TEXT, repository.savedItems.single().type)
        assertEquals(DataItemSource.SHARE_TARGET, repository.savedItems.single().source)
        assertEquals("회의 끝나고 구매 후보 다시 비교하기", repository.savedItems.single().textContent)
        assertEquals(fixedNow, repository.savedItems.single().capturedAtMillis)
    }

    @Test
    fun `url share is saved as link data item`() = runBlocking {
        val repository = FakeDataRepository()
        val handler = ShareContentHandler(repository, nowMillis = { fixedNow })

        val result = handler.save(
            SharePayload(
                action = ShareAction.Send,
                mimeType = "text/plain",
                text = "https://example.com/article?from=share"
            )
        )

        assertEquals(ShareSaveResult.Success(savedCount = 1), result)
        assertEquals(DataItemType.LINK, repository.savedItems.single().type)
        assertEquals("https://example.com/article?from=share", repository.savedItems.single().textContent)
        assertEquals("text/plain", repository.savedItems.single().mimeType)
    }

    @Test
    fun `single image share is saved as image data item`() = runBlocking {
        val repository = FakeDataRepository()
        val handler = ShareContentHandler(repository, nowMillis = { fixedNow })

        val result = handler.save(
            SharePayload(
                action = ShareAction.Send,
                mimeType = "image/png",
                streams = listOf(
                    SharedUri(
                        uri = "content://media/external/images/media/42",
                        mimeType = "image/png"
                    )
                )
            )
        )

        assertEquals(ShareSaveResult.Success(savedCount = 1), result)
        assertEquals(DataItemType.IMAGE, repository.savedItems.single().type)
        assertEquals("content://media/external/images/media/42", repository.savedItems.single().sourceUri)
        assertEquals("image/png", repository.savedItems.single().mimeType)
    }

    @Test
    fun `multiple shared streams are saved once per unique uri`() = runBlocking {
        val repository = FakeDataRepository()
        val handler = ShareContentHandler(repository, nowMillis = { fixedNow })

        val result = handler.save(
            SharePayload(
                action = ShareAction.SendMultiple,
                mimeType = "image/*",
                streams = listOf(
                    SharedUri(uri = "content://media/1", mimeType = "image/jpeg"),
                    SharedUri(uri = "content://media/1", mimeType = "image/jpeg"),
                    SharedUri(uri = "content://downloads/report.pdf", mimeType = "application/pdf")
                )
            )
        )

        assertEquals(ShareSaveResult.PartialSuccess(savedCount = 2, skippedCount = 1), result)
        assertEquals(listOf(DataItemType.IMAGE, DataItemType.FILE), repository.savedItems.map { it.type })
    }

    @Test
    fun `empty share returns failure without saving`() = runBlocking {
        val repository = FakeDataRepository()
        val handler = ShareContentHandler(repository, nowMillis = { fixedNow })

        val result = handler.save(
            SharePayload(
                action = ShareAction.Send,
                mimeType = "text/plain"
            )
        )

        assertEquals(ShareSaveResult.Failure(ShareFailureReason.EmptyPayload), result)
        assertTrue(repository.savedItems.isEmpty())
    }

    private class FakeDataRepository : DataRepository {
        val savedItems = mutableListOf<DataItem>()

        override suspend fun saveDataItem(item: DataItem): Long {
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
