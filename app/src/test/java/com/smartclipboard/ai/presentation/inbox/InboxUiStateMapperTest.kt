package com.smartclipboard.ai.presentation.inbox

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemEnrichment
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemStorage
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.EnrichmentStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InboxUiStateMapperTest {
    @Test
    fun buildsFixedCategoryCardsFromCollectedItems() {
        val state = InboxUiStateMapper.map(
            items = listOf(
                item(id = 1L, type = DataItemType.SCREENSHOT, status = EnrichmentStatus.DONE),
                item(id = 2L, type = DataItemType.DOWNLOAD_IMAGE, status = EnrichmentStatus.PENDING),
                item(id = 3L, type = DataItemType.LINK, status = EnrichmentStatus.FAILED, important = true),
                item(id = 4L, type = DataItemType.TEXT, status = EnrichmentStatus.PROCESSING),
                item(id = 5L, type = DataItemType.FILE, status = EnrichmentStatus.DONE)
            ),
            selectedCategoryId = InboxCategoryId.RECENT,
            viewMode = InboxViewMode.LIST
        )

        assertEquals(
            listOf(
                InboxCategoryId.RECENT,
                InboxCategoryId.IMAGES,
                InboxCategoryId.LINKS,
                InboxCategoryId.TEXTS,
                InboxCategoryId.FILES,
                InboxCategoryId.IMPORTANT,
                InboxCategoryId.PENDING_ANALYSIS
            ),
            state.categories.map { it.id }
        )
        assertEquals(listOf(5, 2, 1, 1, 1, 1, 2), state.categories.map { it.count })
        assertTrue(state.categories.first().isSelected)
        assertEquals(5, state.visibleItems.size)
        assertEquals(InboxViewMode.LIST, state.viewMode)
    }

    @Test
    fun filtersVisibleItemsForSelectedCategoryAndKeepsGridMode() {
        val state = InboxUiStateMapper.map(
            items = listOf(
                item(id = 1L, type = DataItemType.IMAGE, status = EnrichmentStatus.DONE),
                item(
                    id = 2L,
                    type = DataItemType.LINK,
                    status = EnrichmentStatus.DONE,
                    text = "https://example.com",
                    title = "Example"
                ),
                item(id = 3L, type = DataItemType.TEXT, status = EnrichmentStatus.DONE)
            ),
            selectedCategoryId = InboxCategoryId.LINKS,
            viewMode = InboxViewMode.GRID
        )

        assertEquals(InboxCategoryId.LINKS, state.selectedCategoryId)
        assertEquals(InboxViewMode.GRID, state.viewMode)
        assertEquals(1, state.visibleItems.size)
        assertEquals(2L, state.visibleItems.single().id)
        assertEquals("Example", state.visibleItems.single().title)
        assertEquals("링크 · 공유", state.visibleItems.single().meta)
        assertFalse(state.visibleItems.single().isImportant)
    }

    private fun item(
        id: Long,
        type: DataItemType,
        status: EnrichmentStatus,
        important: Boolean = false,
        text: String? = null,
        title: String? = null
    ): DataItem {
        return DataItem(
            id = id,
            type = type,
            source = DataItemSource.SHARE_TARGET,
            textContent = text,
            sourceUri = null,
            capturedAtMillis = 10_000L - id,
            createdAtMillis = 10_000L - id,
            updatedAtMillis = 10_000L - id,
            displayName = if (type == DataItemType.FILE) "file-$id.pdf" else null,
            storage = DataItemStorage(isImportant = important),
            enrichment = DataItemEnrichment(
                status = status,
                ogTitle = title
            )
        )
    }
}
