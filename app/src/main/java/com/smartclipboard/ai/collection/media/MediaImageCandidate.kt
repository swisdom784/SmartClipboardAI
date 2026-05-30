package com.smartclipboard.ai.collection.media

data class MediaImageCandidate(
    val mediaStoreId: Long,
    val contentUri: String,
    val displayName: String?,
    val mimeType: String?,
    val sizeBytes: Long?,
    val addedAtMillis: Long,
    val takenAtMillis: Long?,
    val relativePath: String?
)

interface MediaImageDataSource {
    suspend fun queryImagesAddedBetween(
        fromMillisExclusive: Long,
        toMillisInclusive: Long
    ): List<MediaImageCandidate>
}

interface MediaSyncCheckpointStore {
    var lastSyncMillis: Long?
}

class MediaReadPermissionMissingException : RuntimeException()

sealed interface MediaImportResult {
    data class Imported(
        val importedCount: Int,
        val skippedCount: Int,
        val failedCount: Int
    ) : MediaImportResult
}

sealed interface MediaSyncResult {
    data class Completed(
        val importedCount: Int,
        val skippedCount: Int,
        val failedCount: Int
    ) : MediaSyncResult

    data object PermissionMissing : MediaSyncResult
    data class Failed(val reason: String?) : MediaSyncResult
}

interface MediaImageImporter {
    suspend fun importImages(candidates: List<MediaImageCandidate>): MediaImportResult
}
