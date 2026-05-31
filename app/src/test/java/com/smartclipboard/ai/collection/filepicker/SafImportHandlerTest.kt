package com.smartclipboard.ai.collection.filepicker

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.Topic
import com.smartclipboard.ai.domain.model.TopicAction
import com.smartclipboard.ai.domain.model.TopicAnalysis
import com.smartclipboard.ai.domain.model.TopicItemSelectedBy
import com.smartclipboard.ai.domain.repository.DataRepository
import com.smartclipboard.ai.processing.enrichment.DataItemEnrichmentTrigger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SafImportHandlerTest {
    private val fixedNow = 1_717_300_000_000L

    @Test
    fun `selected image is saved as SAF image data item`() = runBlocking {
        val repository = FakeDataRepository()
        val handler = SafImportHandler(repository, nowMillis = { fixedNow })

        val result = handler.importFiles(
            listOf(
                SafPickedFile(
                    uri = "content://picker/image/1",
                    mimeType = "image/png",
                    displayName = "photo.png",
                    sizeBytes = 1234L
                )
            )
        )

        assertEquals(SafImportResult.Imported(importedCount = 1, skippedCount = 0, failedCount = 0), result)
        assertEquals(DataItemType.IMAGE, repository.savedItems.single().type)
        assertEquals(DataItemSource.SAF, repository.savedItems.single().source)
        assertEquals("content://picker/image/1", repository.savedItems.single().sourceUri)
        assertEquals(fixedNow, repository.savedItems.single().capturedAtMillis)
    }

    @Test
    fun `successful SAF import triggers pending enrichment`() = runBlocking {
        val repository = FakeDataRepository()
        val enrichmentTrigger = FakeEnrichmentTrigger()
        val handler = SafImportHandler(
            repository = repository,
            enrichmentTrigger = enrichmentTrigger,
            nowMillis = { fixedNow }
        )

        handler.importFiles(
            listOf(
                SafPickedFile(
                    uri = "content://picker/image/1",
                    mimeType = "image/png"
                ),
                SafPickedFile(
                    uri = "content://picker/image/2",
                    mimeType = "image/png"
                )
            )
        )

        assertEquals(listOf(2), enrichmentTrigger.savedCounts)
    }

    @Test
    fun `selected pdf is saved as SAF file data item`() = runBlocking {
        val repository = FakeDataRepository()
        val handler = SafImportHandler(repository, nowMillis = { fixedNow })

        handler.importFiles(
            listOf(
                SafPickedFile(
                    uri = "content://picker/file/2",
                    mimeType = "application/pdf",
                    displayName = "report.pdf",
                    sizeBytes = 2048L
                )
            )
        )

        assertEquals(DataItemType.FILE, repository.savedItems.single().type)
        assertEquals("report.pdf", repository.savedItems.single().displayName)
        assertEquals(2048L, repository.savedItems.single().sizeBytes)
    }

    @Test
    fun `empty selection returns empty result without saving`() = runBlocking {
        val repository = FakeDataRepository()
        val handler = SafImportHandler(repository, nowMillis = { fixedNow })

        val result = handler.importFiles(emptyList())

        assertEquals(SafImportResult.EmptySelection, result)
        assertTrue(repository.savedItems.isEmpty())
    }

    @Test
    fun `existing and duplicated uris are skipped`() = runBlocking {
        val existingItem = DataItem(
            type = DataItemType.FILE,
            source = DataItemSource.SAF,
            sourceUri = "content://picker/file/3",
            capturedAtMillis = 1000L,
            createdAtMillis = 1000L,
            updatedAtMillis = 1000L
        )
        val repository = FakeDataRepository(existingItems = listOf(existingItem))
        val handler = SafImportHandler(repository, nowMillis = { fixedNow })

        val result = handler.importFiles(
            listOf(
                SafPickedFile(uri = "content://picker/file/3", mimeType = "application/pdf"),
                SafPickedFile(uri = "content://picker/file/4", mimeType = "application/pdf"),
                SafPickedFile(uri = "content://picker/file/4", mimeType = "application/pdf")
            )
        )

        assertEquals(SafImportResult.Imported(importedCount = 1, skippedCount = 2, failedCount = 0), result)
        assertEquals("content://picker/file/4", repository.savedItems.single().sourceUri)
    }

    @Test
    fun `save failures are counted without stopping remaining files`() = runBlocking {
        val repository = FakeDataRepository(failUris = setOf("content://picker/file/6"))
        val handler = SafImportHandler(repository, nowMillis = { fixedNow })

        val result = handler.importFiles(
            listOf(
                SafPickedFile(uri = "content://picker/file/5", mimeType = "application/pdf"),
                SafPickedFile(uri = "content://picker/file/6", mimeType = "application/pdf"),
                SafPickedFile(uri = "content://picker/file/7", mimeType = "application/pdf")
            )
        )

        assertEquals(SafImportResult.Imported(importedCount = 2, skippedCount = 0, failedCount = 1), result)
        assertEquals(
            listOf("content://picker/file/5", "content://picker/file/7"),
            repository.savedItems.map { it.sourceUri }
        )
    }

    private class FakeDataRepository(
        private val existingItems: List<DataItem> = emptyList(),
        private val failUris: Set<String> = emptySet()
    ) : DataRepository {
        val savedItems = mutableListOf<DataItem>()

        override suspend fun saveDataItem(item: DataItem): Long {
            if (item.sourceUri in failUris) {
                error("Save failed")
            }
            savedItems += item
            return savedItems.size.toLong()
        }

        override suspend fun getDataItem(id: Long): DataItem? = null

        override fun observeDataItems(): Flow<List<DataItem>> = flowOf(existingItems + savedItems)

        override fun observeDataItemsByType(types: Set<DataItemType>): Flow<List<DataItem>> {
            return flowOf((existingItems + savedItems).filter { it.type in types })
        }

        override suspend fun createTopic(
            topic: Topic,
            dataItemIds: List<Long>,
            selectedBy: TopicItemSelectedBy
        ): Long = error("Not used in this test")

        override fun observeTopics(): Flow<List<Topic>> = error("Not used in this test")

        override fun observeDataItemsForTopic(topicId: Long): Flow<List<DataItem>> = error("Not used in this test")

        override suspend fun saveTopicAnalysis(analysis: TopicAnalysis): Long = error("Not used in this test")

        override fun observeTopicAnalyses(topicId: Long): Flow<List<TopicAnalysis>> = error("Not used in this test")

        override suspend fun saveTopicAction(action: TopicAction): Long = error("Not used in this test")

        override suspend fun updateTopicAction(action: TopicAction) = error("Not used in this test")

        override fun observeTopicActions(topicId: Long): Flow<List<TopicAction>> = error("Not used in this test")
    }

    private class FakeEnrichmentTrigger : DataItemEnrichmentTrigger {
        val savedCounts = mutableListOf<Int>()

        override suspend fun runAfterDataInput(savedCount: Int) {
            savedCounts += savedCount
        }
    }
}
