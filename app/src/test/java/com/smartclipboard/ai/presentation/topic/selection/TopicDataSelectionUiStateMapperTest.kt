package com.smartclipboard.ai.presentation.topic.selection

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemEnrichment
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TopicDataSelectionUiStateMapperTest {
    @Test
    fun mapsSelectedSummaryAndSelectableItems() {
        val state = TopicDataSelectionUiStateMapper.map(
            topicId = 9L,
            allItems = listOf(
                dataItem(
                    id = 1L,
                    type = DataItemType.LINK,
                    capturedAtMillis = 10L,
                    enrichment = DataItemEnrichment(
                        ogTitle = "예약 페이지",
                        ogDescription = "항공권 예약"
                    )
                ),
                dataItem(
                    id = 2L,
                    type = DataItemType.SCREENSHOT,
                    capturedAtMillis = 30L,
                    displayName = "screenshot.png"
                ),
                dataItem(
                    id = 3L,
                    type = DataItemType.TEXT,
                    capturedAtMillis = 20L,
                    textContent = "회의 메모"
                )
            ),
            selectedDataItemIds = setOf(1L, 2L)
        )

        assertEquals(9L, state.topicId)
        assertEquals("사용된 자료 2개", state.summary.title)
        assertEquals("이미지 1 · 링크 1", state.summary.subtitle)
        assertEquals(listOf(2L, 1L, 3L), state.items.map { it.id })
        assertTrue(state.items[0].isSelected)
        assertTrue(state.items[1].isSelected)
        assertFalse(state.items[2].isSelected)
        assertEquals("screenshot.png", state.items[0].title)
        assertEquals("예약 페이지", state.items[1].title)
    }

    @Test
    fun describesEmptySelectionWithoutExpandingMaterials() {
        val state = TopicDataSelectionUiStateMapper.map(
            topicId = 4L,
            allItems = listOf(dataItem(id = 1L, type = DataItemType.FILE)),
            selectedDataItemIds = emptySet()
        )

        assertEquals("사용된 자료 0개", state.summary.title)
        assertEquals("분석에 사용할 자료를 선택하세요.", state.summary.subtitle)
    }

    private fun dataItem(
        id: Long,
        type: DataItemType,
        capturedAtMillis: Long = id,
        textContent: String? = "item $id",
        displayName: String? = null,
        enrichment: DataItemEnrichment = DataItemEnrichment()
    ): DataItem {
        return DataItem(
            id = id,
            type = type,
            source = DataItemSource.MANUAL,
            textContent = textContent,
            displayName = displayName,
            capturedAtMillis = capturedAtMillis,
            createdAtMillis = capturedAtMillis,
            updatedAtMillis = capturedAtMillis,
            enrichment = enrichment
        )
    }
}
