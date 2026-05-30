package com.smartclipboard.ai.collection.media

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class MediaSyncManagerTest {
    @Test
    fun `sync queries images between last sync and now then advances checkpoint on clean import`() = runBlocking {
        val dataSource = FakeMediaImageDataSource(
            candidates = listOf(
                MediaImageCandidate(
                    mediaStoreId = 1L,
                    contentUri = "content://media/1",
                    displayName = "image.jpg",
                    mimeType = "image/jpeg",
                    sizeBytes = 100L,
                    addedAtMillis = 4000L,
                    takenAtMillis = null,
                    relativePath = "Pictures/"
                )
            )
        )
        val checkpointStore = FakeMediaSyncCheckpointStore(lastSyncMillis = 1000L)
        val importHandler = FakeMediaImportHandler(
            result = MediaImportResult.Imported(importedCount = 1, skippedCount = 0, failedCount = 0)
        )
        val manager = MediaSyncManager(
            dataSource = dataSource,
            checkpointStore = checkpointStore,
            importHandler = importHandler,
            nowMillis = { 5000L }
        )

        val result = manager.syncNewImages()

        assertEquals(MediaSyncResult.Completed(importedCount = 1, skippedCount = 0, failedCount = 0), result)
        assertEquals(1000L to 5000L, dataSource.lastQueryWindow)
        assertEquals(5000L, checkpointStore.lastSyncMillis)
    }

    @Test
    fun `sync does not advance checkpoint when import has failures`() = runBlocking {
        val dataSource = FakeMediaImageDataSource(candidates = listOf(candidate(2L)))
        val checkpointStore = FakeMediaSyncCheckpointStore(lastSyncMillis = 1000L)
        val importHandler = FakeMediaImportHandler(
            result = MediaImportResult.Imported(importedCount = 0, skippedCount = 0, failedCount = 1)
        )
        val manager = MediaSyncManager(
            dataSource = dataSource,
            checkpointStore = checkpointStore,
            importHandler = importHandler,
            nowMillis = { 5000L }
        )

        val result = manager.syncNewImages()

        assertEquals(MediaSyncResult.Completed(importedCount = 0, skippedCount = 0, failedCount = 1), result)
        assertEquals(1000L, checkpointStore.lastSyncMillis)
    }

    @Test
    fun `sync returns permission missing when datasource cannot read images`() = runBlocking {
        val dataSource = FakeMediaImageDataSource(throwPermissionMissing = true)
        val checkpointStore = FakeMediaSyncCheckpointStore(lastSyncMillis = 1000L)
        val manager = MediaSyncManager(
            dataSource = dataSource,
            checkpointStore = checkpointStore,
            importHandler = FakeMediaImportHandler(MediaImportResult.Imported(0, 0, 0)),
            nowMillis = { 5000L }
        )

        val result = manager.syncNewImages()

        assertEquals(MediaSyncResult.PermissionMissing, result)
        assertEquals(1000L, checkpointStore.lastSyncMillis)
    }

    private fun candidate(mediaStoreId: Long): MediaImageCandidate {
        return MediaImageCandidate(
            mediaStoreId = mediaStoreId,
            contentUri = "content://media/$mediaStoreId",
            displayName = "image_$mediaStoreId.jpg",
            mimeType = "image/jpeg",
            sizeBytes = 100L,
            addedAtMillis = 4000L,
            takenAtMillis = null,
            relativePath = "Pictures/"
        )
    }

    private class FakeMediaImageDataSource(
        private val candidates: List<MediaImageCandidate> = emptyList(),
        private val throwPermissionMissing: Boolean = false
    ) : MediaImageDataSource {
        var lastQueryWindow: Pair<Long, Long>? = null

        override suspend fun queryImagesAddedBetween(
            fromMillisExclusive: Long,
            toMillisInclusive: Long
        ): List<MediaImageCandidate> {
            if (throwPermissionMissing) {
                throw MediaReadPermissionMissingException()
            }
            lastQueryWindow = fromMillisExclusive to toMillisInclusive
            return candidates
        }
    }

    private class FakeMediaSyncCheckpointStore(
        override var lastSyncMillis: Long?
    ) : MediaSyncCheckpointStore

    private class FakeMediaImportHandler(
        private val result: MediaImportResult
    ) : MediaImageImporter {
        override suspend fun importImages(candidates: List<MediaImageCandidate>): MediaImportResult {
            return result
        }
    }
}
