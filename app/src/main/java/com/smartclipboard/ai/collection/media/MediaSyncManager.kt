package com.smartclipboard.ai.collection.media

import javax.inject.Inject

class MediaSyncManager internal constructor(
    private val dataSource: MediaImageDataSource,
    private val checkpointStore: MediaSyncCheckpointStore,
    private val importHandler: MediaImageImporter,
    private val nowMillis: () -> Long = { System.currentTimeMillis() }
) {
    @Inject
    constructor(
        dataSource: AndroidMediaStoreDataSource,
        checkpointStore: SharedPreferencesMediaSyncCheckpointStore,
        importHandler: MediaImportHandler
    ) : this(
        dataSource = dataSource as MediaImageDataSource,
        checkpointStore = checkpointStore as MediaSyncCheckpointStore,
        importHandler = importHandler as MediaImageImporter
    )

    suspend fun syncNewImages(): MediaSyncResult {
        val fromMillisExclusive = checkpointStore.lastSyncMillis ?: 0L
        val toMillisInclusive = nowMillis()

        return try {
            val candidates = dataSource.queryImagesAddedBetween(
                fromMillisExclusive = fromMillisExclusive,
                toMillisInclusive = toMillisInclusive
            )
            val importResult = importHandler.importImages(candidates)
            val completed = importResult.toSyncResult()

            if (completed.failedCount == 0) {
                checkpointStore.lastSyncMillis = toMillisInclusive
            }

            completed
        } catch (_: MediaReadPermissionMissingException) {
            MediaSyncResult.PermissionMissing
        } catch (exception: Exception) {
            MediaSyncResult.Failed(exception.message)
        }
    }

    private fun MediaImportResult.toSyncResult(): MediaSyncResult.Completed {
        return when (this) {
            is MediaImportResult.Imported -> MediaSyncResult.Completed(
                importedCount = importedCount,
                skippedCount = skippedCount,
                failedCount = failedCount
            )
        }
    }
}
