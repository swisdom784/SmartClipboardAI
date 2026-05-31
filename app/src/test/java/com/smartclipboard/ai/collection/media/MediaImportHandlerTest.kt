package com.smartclipboard.ai.collection.media

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
import org.junit.Test

class MediaImportHandlerTest {
    @Test
    fun `new camera image is imported as mediastore image data item`() = runBlocking {
        val repository = FakeDataRepository()
        val handler = MediaImportHandler(repository)

        val result = handler.importImages(
            listOf(
                mediaCandidate(
                    mediaStoreId = 10L,
                    contentUri = "content://media/external/images/media/10",
                    displayName = "IMG_001.jpg",
                    relativePath = "DCIM/Camera/"
                )
            )
        )

        assertEquals(MediaImportResult.Imported(importedCount = 1, skippedCount = 0, failedCount = 0), result)
        assertEquals(DataItemType.IMAGE, repository.savedItems.single().type)
        assertEquals(DataItemSource.MEDIASTORE, repository.savedItems.single().source)
        assertEquals(10L, repository.savedItems.single().mediaStoreId)
        assertEquals("content://media/external/images/media/10", repository.savedItems.single().sourceUri)
    }

    @Test
    fun `successful media import triggers pending enrichment`() = runBlocking {
        val repository = FakeDataRepository()
        val enrichmentTrigger = FakeEnrichmentTrigger()
        val handler = MediaImportHandler(
            repository = repository,
            enrichmentTrigger = enrichmentTrigger
        )

        handler.importImages(
            listOf(
                mediaCandidate(mediaStoreId = 10L, contentUri = "content://media/10"),
                mediaCandidate(mediaStoreId = 11L, contentUri = "content://media/11")
            )
        )

        assertEquals(listOf(2), enrichmentTrigger.savedCounts)
    }

    @Test
    fun `screenshot and download image are classified by path and file name`() = runBlocking {
        val repository = FakeDataRepository()
        val handler = MediaImportHandler(repository)

        handler.importImages(
            listOf(
                mediaCandidate(
                    mediaStoreId = 11L,
                    contentUri = "content://media/11",
                    displayName = "Screenshot_20260530.png",
                    relativePath = "Pictures/Screenshots/"
                ),
                mediaCandidate(
                    mediaStoreId = 12L,
                    contentUri = "content://media/12",
                    displayName = "downloaded.png",
                    relativePath = "Download/"
                )
            )
        )

        assertEquals(
            listOf(DataItemType.SCREENSHOT, DataItemType.DOWNLOAD_IMAGE),
            repository.savedItems.map { it.type }
        )
    }

    @Test
    fun `existing and duplicated media candidates are skipped`() = runBlocking {
        val existingItem = DataItem(
            type = DataItemType.IMAGE,
            source = DataItemSource.MEDIASTORE,
            sourceUri = "content://media/20",
            capturedAtMillis = 1000L,
            createdAtMillis = 1000L,
            updatedAtMillis = 1000L,
            mediaStoreId = 20L
        )
        val repository = FakeDataRepository(existingItems = listOf(existingItem))
        val handler = MediaImportHandler(repository)

        val result = handler.importImages(
            listOf(
                mediaCandidate(mediaStoreId = 20L, contentUri = "content://media/20"),
                mediaCandidate(mediaStoreId = 21L, contentUri = "content://media/21"),
                mediaCandidate(mediaStoreId = 21L, contentUri = "content://media/21")
            )
        )

        assertEquals(MediaImportResult.Imported(importedCount = 1, skippedCount = 2, failedCount = 0), result)
        assertEquals(21L, repository.savedItems.single().mediaStoreId)
    }

    @Test
    fun `save failures are counted without stopping remaining imports`() = runBlocking {
        val repository = FakeDataRepository(failMediaStoreIds = setOf(31L))
        val handler = MediaImportHandler(repository)

        val result = handler.importImages(
            listOf(
                mediaCandidate(mediaStoreId = 30L, contentUri = "content://media/30"),
                mediaCandidate(mediaStoreId = 31L, contentUri = "content://media/31"),
                mediaCandidate(mediaStoreId = 32L, contentUri = "content://media/32")
            )
        )

        assertEquals(MediaImportResult.Imported(importedCount = 2, skippedCount = 0, failedCount = 1), result)
        assertEquals(listOf(30L, 32L), repository.savedItems.map { it.mediaStoreId })
    }

    private fun mediaCandidate(
        mediaStoreId: Long,
        contentUri: String,
        displayName: String = "image_$mediaStoreId.jpg",
        relativePath: String? = "Pictures/",
        addedAtMillis: Long = 1_717_200_000_000L
    ): MediaImageCandidate {
        return MediaImageCandidate(
            mediaStoreId = mediaStoreId,
            contentUri = contentUri,
            displayName = displayName,
            mimeType = "image/jpeg",
            sizeBytes = 1024L,
            addedAtMillis = addedAtMillis,
            takenAtMillis = null,
            relativePath = relativePath
        )
    }

    private class FakeDataRepository(
        private val existingItems: List<DataItem> = emptyList(),
        private val failMediaStoreIds: Set<Long> = emptySet()
    ) : DataRepository {
        val savedItems = mutableListOf<DataItem>()

        override suspend fun saveDataItem(item: DataItem): Long {
            if (item.mediaStoreId in failMediaStoreIds) {
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
