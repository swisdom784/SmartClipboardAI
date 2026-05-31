package com.smartclipboard.ai.processing.enrichment

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.EnrichmentStatus
import com.smartclipboard.ai.processing.ocr.OcrProcessor
import com.smartclipboard.ai.processing.web.OpenGraphMetadata
import com.smartclipboard.ai.processing.web.WebExtractor
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class DataItemEnrichmentManager @Inject constructor(
    private val store: DataItemEnrichmentStore,
    private val webExtractor: WebExtractor,
    private val ocrProcessor: OcrProcessor
) {
    internal constructor(
        store: DataItemEnrichmentStore,
        webExtractor: WebExtractor,
        ocrProcessor: OcrProcessor,
        nowMillis: () -> Long
    ) : this(store, webExtractor, ocrProcessor) {
        this.nowMillis = nowMillis
    }

    private var nowMillis: () -> Long = { System.currentTimeMillis() }

    suspend fun runPending(limit: Int = DEFAULT_LIMIT): EnrichmentRunResult {
        val pendingItems = store.getPendingItems(limit)
        var successCount = 0
        var retryCount = 0
        var failedCount = 0
        var skippedCount = 0

        pendingItems.forEach { item ->
            try {
                val updatedItem = enrich(item)
                store.updateDataItem(updatedItem)
                successCount += 1
            } catch (_: UnsupportedEnrichmentException) {
                skippedCount += 1
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                val failedItem = item.withFailureState()
                store.updateDataItem(failedItem)
                if (failedItem.enrichment.status == EnrichmentStatus.FAILED) {
                    failedCount += 1
                } else {
                    retryCount += 1
                }
            }
        }

        return EnrichmentRunResult(
            processedCount = pendingItems.size,
            successCount = successCount,
            retryCount = retryCount,
            failedCount = failedCount,
            skippedCount = skippedCount
        )
    }

    private suspend fun enrich(item: DataItem): DataItem {
        return when (item.type) {
            DataItemType.LINK -> {
                val url = item.resolveUrl()
                    ?: throw UnsupportedEnrichmentException()
                item.withOpenGraphMetadata(webExtractor.extract(url))
            }

            DataItemType.IMAGE,
            DataItemType.SCREENSHOT,
            DataItemType.DOWNLOAD_IMAGE -> {
                val uri = item.sourceUri
                    ?: item.storage.internalUri
                    ?: throw UnsupportedEnrichmentException()
                item.withOcrText(ocrProcessor.recognizeText(uri))
            }

            DataItemType.TEXT,
            DataItemType.FILE -> throw UnsupportedEnrichmentException()
        }
    }

    private fun DataItem.withOpenGraphMetadata(metadata: OpenGraphMetadata): DataItem {
        return copy(
            updatedAtMillis = nowMillis(),
            enrichment = enrichment.copy(
                status = EnrichmentStatus.DONE,
                ogTitle = metadata.title,
                ogDescription = metadata.description,
                ogImageUrl = metadata.imageUrl
            )
        )
    }

    private fun DataItem.withOcrText(text: String): DataItem {
        return copy(
            updatedAtMillis = nowMillis(),
            enrichment = enrichment.copy(
                status = EnrichmentStatus.DONE,
                ocrText = text
            )
        )
    }

    private fun DataItem.withFailureState(): DataItem {
        val nextRetryCount = enrichment.retryCount + 1
        val nextStatus = if (nextRetryCount >= MAX_RETRIES) {
            EnrichmentStatus.FAILED
        } else {
            EnrichmentStatus.PENDING
        }

        return copy(
            updatedAtMillis = nowMillis(),
            enrichment = enrichment.copy(
                status = nextStatus,
                retryCount = nextRetryCount
            )
        )
    }

    private fun DataItem.resolveUrl(): String? {
        return sourceUri?.takeIf { it.isHttpUrl() }
            ?: textContent?.let { URL_REGEX.find(it)?.value?.trimUrlSuffix() }
    }

    private fun String.isHttpUrl(): Boolean {
        return startsWith("http://") || startsWith("https://")
    }

    private fun String.trimUrlSuffix(): String {
        return trimEnd('.', ',', ')', ']', '}')
    }

    private class UnsupportedEnrichmentException : RuntimeException()

    private companion object {
        const val DEFAULT_LIMIT = 20
        const val MAX_RETRIES = 3
        val URL_REGEX = Regex("""https?://\S+""")
    }
}
