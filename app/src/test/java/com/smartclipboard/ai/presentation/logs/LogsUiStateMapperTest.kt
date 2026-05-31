package com.smartclipboard.ai.presentation.logs

import com.smartclipboard.ai.domain.model.Topic
import com.smartclipboard.ai.domain.model.TopicOrigin
import com.smartclipboard.ai.domain.model.TopicStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LogsUiStateMapperTest {
    @Test
    fun mapsUserVisibleTopicsIntoLogEntriesAndFilters() {
        val state = LogsUiStateMapper.map(
            topics = listOf(
                topic(id = 1L, title = "출장 준비", origin = TopicOrigin.USER_REQUEST, status = TopicStatus.ACTIVE),
                topic(id = 2L, title = "회의 요약", origin = TopicOrigin.AI_RECOMMENDATION, status = TopicStatus.COMPLETED),
                topic(id = 3L, title = "구매 후보", origin = TopicOrigin.AI_RECOMMENDATION, status = TopicStatus.INCOMPLETE)
            ),
            selectedFilter = LogFilterId.ALL
        )

        assertEquals(3, state.visibleEntries.size)
        assertEquals(listOf(3, 1, 2, 1, 1, 1), state.filters.map { it.count })
        assertEquals("구매 후보", state.visibleEntries[0].title)
        assertTrue(state.visibleEntries[0].badges.contains(LogBadge.AI_RECOMMENDATION))
        assertTrue(state.visibleEntries[0].badges.contains(LogBadge.INCOMPLETE))
    }

    @Test
    fun filtersCompletedLogsOnly() {
        val state = LogsUiStateMapper.map(
            topics = listOf(
                topic(id = 1L, title = "진행 중", origin = TopicOrigin.USER_REQUEST, status = TopicStatus.ACTIVE),
                topic(id = 2L, title = "완료됨", origin = TopicOrigin.AI_RECOMMENDATION, status = TopicStatus.COMPLETED)
            ),
            selectedFilter = LogFilterId.COMPLETED
        )

        assertEquals(LogFilterId.COMPLETED, state.selectedFilter)
        assertEquals(1, state.visibleEntries.size)
        assertEquals("완료됨", state.visibleEntries.single().title)
        assertTrue(state.visibleEntries.single().badges.contains(LogBadge.COMPLETED))
    }

    private fun topic(
        id: Long,
        title: String,
        origin: TopicOrigin,
        status: TopicStatus
    ): Topic {
        return Topic(
            id = id,
            title = title,
            prompt = "작업 프롬프트",
            origin = origin,
            status = status,
            createdAtMillis = id * 100L,
            updatedAtMillis = id * 100L,
            completedAtMillis = if (status == TopicStatus.COMPLETED) id * 100L else null
        )
    }
}
