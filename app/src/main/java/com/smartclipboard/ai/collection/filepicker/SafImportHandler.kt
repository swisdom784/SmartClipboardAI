package com.smartclipboard.ai.collection.filepicker

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.repository.DataRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class SafImportHandler @Inject constructor(
    private val repository: DataRepository
) {
    private var nowMillis: () -> Long = { System.currentTimeMillis() }

    internal constructor(
        repository: DataRepository,
        nowMillis: () -> Long
    ) : this(repository) {
        this.nowMillis = nowMillis
    }

    suspend fun importFiles(files: List<SafPickedFile>): SafImportResult {
        val nonBlankFiles = files.filter { it.uri.isNotBlank() }
        if (nonBlankFiles.isEmpty()) {
            return SafImportResult.EmptySelection
        }

        val existingUris = repository.observeDataItemsByType(SAF_TYPES)
            .first()
            .mapNotNull { it.sourceUri }
            .toSet()
        val seenUris = mutableSetOf<String>()
        var importedCount = 0
        var skippedCount = 0
        var failedCount = 0

        nonBlankFiles.forEach { file ->
            if (file.uri in existingUris || file.uri in seenUris) {
                skippedCount += 1
                return@forEach
            }

            seenUris += file.uri

            try {
                repository.saveDataItem(file.toDataItem(nowMillis()))
                importedCount += 1
            } catch (_: Exception) {
                failedCount += 1
            }
        }

        return SafImportResult.Imported(
            importedCount = importedCount,
            skippedCount = skippedCount,
            failedCount = failedCount
        )
    }

    private fun SafPickedFile.toDataItem(now: Long): DataItem {
        return DataItem(
            type = if (mimeType?.startsWith("image/") == true) DataItemType.IMAGE else DataItemType.FILE,
            source = DataItemSource.SAF,
            sourceUri = uri,
            capturedAtMillis = now,
            createdAtMillis = now,
            updatedAtMillis = now,
            mimeType = mimeType,
            displayName = displayName,
            sizeBytes = sizeBytes
        )
    }

    private companion object {
        val SAF_TYPES = setOf(DataItemType.IMAGE, DataItemType.FILE)
    }
}
