package com.smartclipboard.ai.collection.clipboard

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.repository.DataRepository
import com.smartclipboard.ai.processing.enrichment.DataItemEnrichmentTrigger
import com.smartclipboard.ai.processing.enrichment.NoOpDataItemEnrichmentTrigger
import javax.inject.Inject

class ClipboardCaptureHandler @Inject constructor(
    private val repository: DataRepository,
    private val enrichmentTrigger: DataItemEnrichmentTrigger
) {
    private var nowMillis: () -> Long = { System.currentTimeMillis() }

    internal constructor(
        repository: DataRepository,
        nowMillis: () -> Long
    ) : this(repository, NoOpDataItemEnrichmentTrigger) {
        this.nowMillis = nowMillis
    }

    internal constructor(
        repository: DataRepository,
        enrichmentTrigger: DataItemEnrichmentTrigger,
        nowMillis: () -> Long
    ) : this(repository, enrichmentTrigger) {
        this.nowMillis = nowMillis
    }

    suspend fun save(payload: ClipboardPayload): ClipboardCaptureResult {
        val trimmedText = payload.text?.trim()

        if (trimmedText.isNullOrEmpty()) {
            return if (payload.hasUnsupportedContent) {
                ClipboardCaptureResult.Failure(ClipboardFailureReason.UnsupportedContent)
            } else {
                ClipboardCaptureResult.Failure(ClipboardFailureReason.EmptyClipboard)
            }
        }

        val now = nowMillis()
        val url = trimmedText.firstUrlOrNull()
        val item = DataItem(
            type = if (url != null) DataItemType.LINK else DataItemType.TEXT,
            source = DataItemSource.CLIPBOARD_TILE,
            textContent = trimmedText,
            sourceUri = url,
            capturedAtMillis = now,
            createdAtMillis = now,
            updatedAtMillis = now,
            mimeType = "text/plain"
        )

        return try {
            repository.saveDataItem(item)
            runEnrichmentAfterInput(savedCount = 1)
            ClipboardCaptureResult.Success(savedCount = 1)
        } catch (_: Exception) {
            ClipboardCaptureResult.Failure(ClipboardFailureReason.SaveFailed)
        }
    }

    private suspend fun runEnrichmentAfterInput(savedCount: Int) {
        runCatching { enrichmentTrigger.runAfterDataInput(savedCount) }
    }

    private fun String.firstUrlOrNull(): String? {
        return URL_PATTERN.find(this)?.value
    }

    private companion object {
        val URL_PATTERN = Regex("""https?://[^\s]+""")
    }
}
