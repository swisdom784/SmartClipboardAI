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

    @Test
    fun limitsSelectableItemsForLargeLibrariesWhileKeepingSelectedItems() {
        val state = TopicDataSelectionUiStateMapper.map(
            topicId = 7L,
            allItems = (1L..250L).map { id ->
                dataItem(
                    id = id,
                    type = DataItemType.IMAGE,
                    capturedAtMillis = id
                )
            },
            selectedDataItemIds = setOf(1L)
        )

        assertEquals(200, state.items.size)
        assertTrue(state.items.any { it.id == 1L })
        assertTrue(state.items.any { it.id == 250L })
    }

    @Test
    fun exposesDisplayLimitTextForLargeLibraries() {
        val state = TopicDataSelectionUiStateMapper.map(
            topicId = 7L,
            allItems = (1L..250L).map { id ->
                dataItem(
                    id = id,
                    type = DataItemType.IMAGE,
                    capturedAtMillis = id
                )
            },
            selectedDataItemIds = emptySet()
        )

        assertEquals("최근 자료 200개 표시 · 전체 250개", state.displayLimitText)
    }

    @Test
    fun filtersSelectableItemsByTypeAndSource() {
        val imageState = TopicDataSelectionUiStateMapper.map(
            topicId = 7L,
            allItems = listOf(
                dataItem(id = 1L, type = DataItemType.LINK),
                dataItem(id = 2L, type = DataItemType.SCREENSHOT),
                dataItem(id = 3L, type = DataItemType.TEXT)
            ),
            selectedDataItemIds = emptySet(),
            selectedFilter = TopicDataSelectionFilter.IMAGE
        )

        assertEquals(TopicDataSelectionFilter.IMAGE, imageState.selectedFilter)
        assertEquals(listOf("이미지"), imageState.items.map { it.typeLabel })
        assertEquals(listOf(2L), imageState.items.map { it.id })

        val sharedState = TopicDataSelectionUiStateMapper.map(
            topicId = 7L,
            allItems = listOf(
                dataItem(id = 4L, type = DataItemType.LINK, source = DataItemSource.SHARE_TARGET),
                dataItem(id = 5L, type = DataItemType.TEXT, source = DataItemSource.MANUAL)
            ),
            selectedDataItemIds = emptySet(),
            selectedFilter = TopicDataSelectionFilter.SHARED
        )

        assertEquals(listOf(4L), sharedState.items.map { it.id })
        assertEquals(
            listOf("최근", "공유", "이미지", "링크", "텍스트", "파일"),
            sharedState.filterOptions.map { it.label }
        )
    }

    @Test
    fun keepsSelectedIdsAndSummaryWhenSelectedItemIsOutsideCurrentFilter() {
        val state = TopicDataSelectionUiStateMapper.map(
            topicId = 7L,
            allItems = listOf(
                dataItem(id = 1L, type = DataItemType.IMAGE),
                dataItem(id = 2L, type = DataItemType.LINK)
            ),
            selectedDataItemIds = setOf(1L),
            selectedFilter = TopicDataSelectionFilter.LINK
        )

        assertEquals(setOf(1L), state.selectedDataItemIds)
        assertEquals("사용된 자료 1개", state.summary.title)
        assertEquals("이미지 1", state.summary.subtitle)
        assertEquals(listOf(2L), state.items.map { it.id })
    }

    private fun dataItem(
        id: Long,
        type: DataItemType,
        source: DataItemSource = DataItemSource.MANUAL,
        capturedAtMillis: Long = id,
        textContent: String? = "item $id",
        displayName: String? = null,
        enrichment: DataItemEnrichment = DataItemEnrichment()
    ): DataItem {
        return DataItem(
            id = id,
            type = type,
            source = source,
            textContent = textContent,
            displayName = displayName,
            capturedAtMillis = capturedAtMillis,
            createdAtMillis = capturedAtMillis,
            updatedAtMillis = capturedAtMillis,
            enrichment = enrichment
        )
    }
}
