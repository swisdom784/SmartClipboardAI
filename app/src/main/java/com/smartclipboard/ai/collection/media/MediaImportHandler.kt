package com.smartclipboard.ai.collection.media

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.repository.DataRepository
import com.smartclipboard.ai.processing.enrichment.DataItemEnrichmentTrigger
import com.smartclipboard.ai.processing.enrichment.NoOpDataItemEnrichmentTrigger
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class MediaImportHandler @Inject constructor(
    private val repository: DataRepository,
    private val enrichmentTrigger: DataItemEnrichmentTrigger
) : MediaImageImporter {
    internal constructor(
        repository: DataRepository
    ) : this(repository, NoOpDataItemEnrichmentTrigger)

    override suspend fun importImages(candidates: List<MediaImageCandidate>): MediaImportResult {
        val existingItems = repository.observeDataItemsByType(IMAGE_TYPES).first()
        val existingMediaStoreIds = existingItems.mapNotNull { it.mediaStoreId }.toSet()
        val existingUris = existingItems.mapNotNull { it.sourceUri }.toSet()
        val seenKeys = mutableSetOf<String>()

        var skippedCount = 0
        var importedCount = 0
        var failedCount = 0

        candidates.forEach { candidate ->
            val key = candidate.deduplicationKey()
            val alreadyImported = candidate.mediaStoreId in existingMediaStoreIds ||
                candidate.contentUri in existingUris ||
                key in seenKeys

            if (alreadyImported) {
                skippedCount += 1
                return@forEach
            }

            seenKeys += key

            try {
                repository.saveDataItem(candidate.toDataItem())
                importedCount += 1
            } catch (_: Exception) {
                failedCount += 1
            }
        }

        runEnrichmentAfterInput(importedCount)
        return MediaImportResult.Imported(
            importedCount = importedCount,
            skippedCount = skippedCount,
            failedCount = failedCount
        )
    }

    private suspend fun runEnrichmentAfterInput(savedCount: Int) {
        if (savedCount > 0) {
            runCatching { enrichmentTrigger.runAfterDataInput(savedCount) }
        }
    }

    private fun MediaImageCandidate.toDataItem(): DataItem {
        val capturedAtMillis = takenAtMillis ?: addedAtMillis
        return DataItem(
            type = classifyType(),
            source = DataItemSource.MEDIASTORE,
            sourceUri = contentUri,
            capturedAtMillis = capturedAtMillis,
            createdAtMillis = addedAtMillis,
            updatedAtMillis = addedAtMillis,
            lastSyncedAtMillis = System.currentTimeMillis(),
            mimeType = mimeType,
            displayName = displayName,
            sizeBytes = sizeBytes,
            mediaStoreId = mediaStoreId
        )
    }

    private fun MediaImageCandidate.classifyType(): DataItemType {
        val searchable = "${relativePath.orEmpty()}/${displayName.orEmpty()}".lowercase()
        return when {
            "screenshot" in searchable || "screenshots" in searchable -> DataItemType.SCREENSHOT
            "download" in searchable -> DataItemType.DOWNLOAD_IMAGE
            else -> DataItemType.IMAGE
        }
    }

    private fun MediaImageCandidate.deduplicationKey(): String {
        return "$mediaStoreId:$contentUri"
    }

    private companion object {
        val IMAGE_TYPES = setOf(
            DataItemType.IMAGE,
            DataItemType.SCREENSHOT,
            DataItemType.DOWNLOAD_IMAGE
        )
    }
}
