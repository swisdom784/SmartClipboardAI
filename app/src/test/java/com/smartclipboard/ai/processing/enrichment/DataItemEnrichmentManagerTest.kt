package com.smartclipboard.ai.processing.enrichment

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemEnrichment
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.EnrichmentStatus
import com.smartclipboard.ai.processing.ocr.OcrProcessor
import com.smartclipboard.ai.processing.web.OpenGraphMetadata
import com.smartclipboard.ai.processing.web.WebExtractor
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import kotlin.coroutines.cancellation.CancellationException

class DataItemEnrichmentManagerTest {
    private val fixedNow = 1_717_400_000_000L

    @Test
    fun `link item stores open graph metadata as done`() = runBlocking {
        val item = dataItem(
            id = 1L,
            type = DataItemType.LINK,
            textContent = "https://example.com/story",
            sourceUri = "https://example.com/story"
        )
        val store = FakeEnrichmentStore(listOf(item))
        val manager = DataItemEnrichmentManager(
            store = store,
            webExtractor = FakeWebExtractor(
                result = OpenGraphMetadata(
                    title = "Example title",
                    description = "Example description",
                    imageUrl = "https://example.com/og.png"
                )
            ),
            ocrProcessor = FakeOcrProcessor(),
            nowMillis = { fixedNow }
        )

        val result = manager.runPending(limit = 10)

        assertEquals(EnrichmentRunResult(processedCount = 1, successCount = 1, retryCount = 0, failedCount = 0, skippedCount = 0), result)
        val updated = store.updatedItems.single()
        assertEquals(EnrichmentStatus.DONE, updated.enrichment.status)
        assertEquals("Example title", updated.enrichment.ogTitle)
        assertEquals("Example description", updated.enrichment.ogDescription)
        assertEquals("https://example.com/og.png", updated.enrichment.ogImageUrl)
        assertEquals(fixedNow, updated.updatedAtMillis)
    }

    @Test
    fun `image item stores OCR text as done`() = runBlocking {
        val item = dataItem(
            id = 2L,
            type = DataItemType.SCREENSHOT,
            sourceUri = "content://media/2"
        )
        val store = FakeEnrichmentStore(listOf(item))
        val manager = DataItemEnrichmentManager(
            store = store,
            webExtractor = FakeWebExtractor(),
            ocrProcessor = FakeOcrProcessor(result = "제주도 일정 5월 1일"),
            nowMillis = { fixedNow }
        )

        val result = manager.runPending(limit = 10)

        assertEquals(EnrichmentRunResult(processedCount = 1, successCount = 1, retryCount = 0, failedCount = 0, skippedCount = 0), result)
        val updated = store.updatedItems.single()
        assertEquals(EnrichmentStatus.DONE, updated.enrichment.status)
        assertEquals("제주도 일정 5월 1일", updated.enrichment.ocrText)
    }

    @Test
    fun `temporary failure increments retry and keeps item pending`() = runBlocking {
        val item = dataItem(
            id = 3L,
            type = DataItemType.LINK,
            sourceUri = "https://example.com/fail",
            enrichment = DataItemEnrichment(retryCount = 1)
        )
        val store = FakeEnrichmentStore(listOf(item))
        val manager = DataItemEnrichmentManager(
            store = store,
            webExtractor = FakeWebExtractor(throwOnExtract = true),
            ocrProcessor = FakeOcrProcessor(),
            nowMillis = { fixedNow }
        )

        val result = manager.runPending(limit = 10)

        assertEquals(EnrichmentRunResult(processedCount = 1, successCount = 0, retryCount = 1, failedCount = 0, skippedCount = 0), result)
        val updated = store.updatedItems.single()
        assertEquals(EnrichmentStatus.PENDING, updated.enrichment.status)
        assertEquals(2, updated.enrichment.retryCount)
    }

    @Test
    fun `third failure marks item as failed`() = runBlocking {
        val item = dataItem(
            id = 4L,
            type = DataItemType.IMAGE,
            sourceUri = "content://media/fail",
            enrichment = DataItemEnrichment(retryCount = 2)
        )
        val store = FakeEnrichmentStore(listOf(item))
        val manager = DataItemEnrichmentManager(
            store = store,
            webExtractor = FakeWebExtractor(),
            ocrProcessor = FakeOcrProcessor(throwOnRecognize = true),
            nowMillis = { fixedNow }
        )

        val result = manager.runPending(limit = 10)

        assertEquals(EnrichmentRunResult(processedCount = 1, successCount = 0, retryCount = 0, failedCount = 1, skippedCount = 0), result)
        val updated = store.updatedItems.single()
        assertEquals(EnrichmentStatus.FAILED, updated.enrichment.status)
        assertEquals(3, updated.enrichment.retryCount)
    }

    @Test
    fun `cancellation is propagated without retry update`() = runBlocking {
        val item = dataItem(
            id = 5L,
            type = DataItemType.LINK,
            sourceUri = "https://example.com/slow"
        )
        val store = FakeEnrichmentStore(listOf(item))
        val manager = DataItemEnrichmentManager(
            store = store,
            webExtractor = FakeWebExtractor(throwable = CancellationException("Timed out")),
            ocrProcessor = FakeOcrProcessor(),
            nowMillis = { fixedNow }
        )

        try {
            manager.runPending(limit = 10)
            fail("Expected cancellation to be propagated")
        } catch (_: CancellationException) {
            assertEquals(emptyList<DataItem>(), store.updatedItems)
        }
    }

    private fun dataItem(
        id: Long,
        type: DataItemType,
        textContent: String? = null,
        sourceUri: String? = null,
        enrichment: DataItemEnrichment = DataItemEnrichment()
    ): DataItem {
        return DataItem(
            id = id,
            type = type,
            source = DataItemSource.MANUAL,
            textContent = textContent,
            sourceUri = sourceUri,
            capturedAtMillis = 1000L,
            createdAtMillis = 1000L,
            updatedAtMillis = 1000L,
            enrichment = enrichment
        )
    }

    private class FakeEnrichmentStore(
        private val pendingItems: List<DataItem>
    ) : DataItemEnrichmentStore {
        val updatedItems = mutableListOf<DataItem>()

        override suspend fun getPendingItems(limit: Int): List<DataItem> {
            return pendingItems.take(limit)
        }

        override suspend fun updateDataItem(item: DataItem) {
            updatedItems += item
        }
    }

    private class FakeWebExtractor(
        private val result: OpenGraphMetadata = OpenGraphMetadata(),
        private val throwOnExtract: Boolean = false,
        private val throwable: Throwable? = null
    ) : WebExtractor {
        override suspend fun extract(url: String): OpenGraphMetadata {
            throwable?.let { throw it }
            if (throwOnExtract) {
                error("Network failed")
            }
            return result
        }
    }

    private class FakeOcrProcessor(
        private val result: String = "",
        private val throwOnRecognize: Boolean = false
    ) : OcrProcessor {
        override suspend fun recognizeText(uri: String): String {
            if (throwOnRecognize) {
                error("OCR failed")
            }
            return result
        }
    }
}
