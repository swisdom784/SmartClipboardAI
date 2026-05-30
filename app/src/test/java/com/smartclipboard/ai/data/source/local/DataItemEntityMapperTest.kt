package com.smartclipboard.ai.data.source.local

import com.smartclipboard.ai.data.source.local.entity.DataItemEntity
import com.smartclipboard.ai.data.source.local.mapper.toDomain
import com.smartclipboard.ai.data.source.local.mapper.toEntity
import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemCluster
import com.smartclipboard.ai.domain.model.DataItemEnrichment
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemStorage
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.EnrichmentStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class DataItemEntityMapperTest {
    @Test
    fun dataItemEntityRoundTripPreservesCollectionEnrichmentAndStorageFields() {
        val domain = DataItem(
            id = 7L,
            type = DataItemType.LINK,
            source = DataItemSource.SHARE_TARGET,
            textContent = "https://example.com/article",
            sourceUri = "content://shared/item/7",
            capturedAtMillis = 1_700_000_000_000L,
            createdAtMillis = 1_700_000_000_100L,
            updatedAtMillis = 1_700_000_000_200L,
            lastSyncedAtMillis = 1_700_000_000_300L,
            mimeType = "text/plain",
            displayName = "article",
            sizeBytes = 128L,
            mediaStoreId = 99L,
            storage = DataItemStorage(
                internalUri = "content://smartclipboard/internal/7",
                isImportant = true,
                isPreserved = true
            ),
            enrichment = DataItemEnrichment(
                status = EnrichmentStatus.FAILED,
                retryCount = 3,
                ocrText = "ocr text",
                ogTitle = "OG title",
                ogDescription = "OG description",
                ogImageUrl = "https://example.com/og.png",
                geminiSummary = "summary",
                keywords = listOf("travel", "ticket"),
                detectedDateTimeMillis = 1_700_000_001_000L,
                detectedLocation = "Seoul"
            ),
            cluster = DataItemCluster(
                id = "cluster-1",
                label = "여행 준비",
                score = 0.87,
                updatedAtMillis = 1_700_000_002_000L
            )
        )

        val entity = domain.toEntity()
        val restored = entity.toDomain()

        assertEquals(domain, restored)
    }

    @Test
    fun entityWithMissingOptionalValuesMapsToStableDomainDefaults() {
        val entity = DataItemEntity(
            id = 1L,
            type = "IMAGE",
            source = "MEDIASTORE",
            capturedAtMillis = 10L,
            createdAtMillis = 20L,
            updatedAtMillis = 30L
        )

        val restored = entity.toDomain()

        assertEquals(DataItemType.IMAGE, restored.type)
        assertEquals(DataItemSource.MEDIASTORE, restored.source)
        assertEquals(EnrichmentStatus.PENDING, restored.enrichment.status)
        assertEquals(0, restored.enrichment.retryCount)
        assertEquals(emptyList<String>(), restored.enrichment.keywords)
        assertEquals(false, restored.storage.isImportant)
        assertEquals(false, restored.storage.isPreserved)
    }
}
