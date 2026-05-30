package com.smartclipboard.ai.collection.share

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.repository.DataRepository
import javax.inject.Inject

class ShareContentHandler @Inject constructor(
    private val repository: DataRepository
) {
    private var nowMillis: () -> Long = { System.currentTimeMillis() }

    internal constructor(
        repository: DataRepository,
        nowMillis: () -> Long
    ) : this(repository) {
        this.nowMillis = nowMillis
    }

    suspend fun save(payload: SharePayload): ShareSaveResult {
        val now = nowMillis()
        val candidates = payload.toDataItems(now)

        if (candidates.isEmpty()) {
            return ShareSaveResult.Failure(ShareFailureReason.EmptyPayload)
        }

        val uniqueItems = candidates.distinctBy { it.shareDeduplicationKey() }
        var savedCount = 0

        return try {
            uniqueItems.forEach { item ->
                repository.saveDataItem(item)
                savedCount += 1
            }

            val skippedCount = candidates.size - uniqueItems.size
            if (skippedCount > 0) {
                ShareSaveResult.PartialSuccess(
                    savedCount = savedCount,
                    skippedCount = skippedCount
                )
            } else {
                ShareSaveResult.Success(savedCount = savedCount)
            }
        } catch (_: Exception) {
            if (savedCount > 0) {
                ShareSaveResult.PartialSuccess(
                    savedCount = savedCount,
                    skippedCount = uniqueItems.size - savedCount + candidates.size - uniqueItems.size
                )
            } else {
                ShareSaveResult.Failure(ShareFailureReason.SaveFailed)
            }
        }
    }

    private fun SharePayload.toDataItems(now: Long): List<DataItem> {
        val textItem = text
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?.let { sharedText ->
                val url = sharedText.firstUrlOrNull()
                DataItem(
                    type = if (url != null) DataItemType.LINK else DataItemType.TEXT,
                    source = DataItemSource.SHARE_TARGET,
                    textContent = sharedText,
                    sourceUri = url,
                    capturedAtMillis = now,
                    createdAtMillis = now,
                    updatedAtMillis = now,
                    mimeType = mimeType
                )
            }

        val streamItems = streams
            .filter { it.uri.isNotBlank() }
            .map { sharedUri ->
                DataItem(
                    type = sharedUri.toDataItemType(),
                    source = DataItemSource.SHARE_TARGET,
                    sourceUri = sharedUri.uri,
                    capturedAtMillis = now,
                    createdAtMillis = now,
                    updatedAtMillis = now,
                    mimeType = sharedUri.mimeType ?: mimeType,
                    displayName = sharedUri.displayName,
                    sizeBytes = sharedUri.sizeBytes
                )
            }

        return listOfNotNull(textItem) + streamItems
    }

    private fun SharedUri.toDataItemType(): DataItemType {
        return if (mimeType?.startsWith("image/") == true) {
            DataItemType.IMAGE
        } else {
            DataItemType.FILE
        }
    }

    private fun DataItem.shareDeduplicationKey(): String {
        return when {
            sourceUri != null -> "${type.name}:uri:$sourceUri"
            textContent != null -> "${type.name}:text:$textContent"
            else -> "${type.name}:unknown"
        }
    }

    private fun String.firstUrlOrNull(): String? {
        return URL_PATTERN.find(this)?.value
    }

    private companion object {
        val URL_PATTERN = Regex("""https?://[^\s]+""")
    }
}
