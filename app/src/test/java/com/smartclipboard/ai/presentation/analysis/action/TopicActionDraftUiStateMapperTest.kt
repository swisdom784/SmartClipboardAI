package com.smartclipboard.ai.presentation.analysis.action

import com.smartclipboard.ai.domain.model.TopicAction
import com.smartclipboard.ai.domain.model.TopicActionStatus
import com.smartclipboard.ai.domain.model.TopicActionTargetApp
import com.smartclipboard.ai.domain.model.TopicActionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TopicActionDraftUiStateMapperTest {
    @Test
    fun mapsActionCardsWithReviewAndCompletedStates() {
        val state = TopicActionDraftUiStateMapper.map(
            actions = listOf(
                action(
                    id = 1L,
                    type = TopicActionType.NOTE,
                    status = TopicActionStatus.PENDING_REVIEW,
                    previewText = "아주 긴 노트 미리보기입니다. ".repeat(8)
                ),
                action(
                    id = 2L,
                    type = TopicActionType.CALENDAR,
                    status = TopicActionStatus.COMPLETED
                )
            )
        )

        assertEquals(listOf(1L, 2L), state.cards.map { it.id })
        assertEquals("노트", state.cards[0].typeLabel)
        assertEquals("검토 필요", state.cards[0].statusLabel)
        assertFalse(state.cards[0].isCollapsed)
        assertTrue(state.cards[0].previewText.length <= 80)
        assertEquals("캘린더", state.cards[1].typeLabel)
        assertEquals("완료", state.cards[1].statusLabel)
        assertTrue(state.cards[1].isCollapsed)
    }

    @Test
    fun detectsAllRequiredActionsCompleted() {
        val state = TopicActionDraftUiStateMapper.map(
            actions = listOf(
                action(id = 1L, type = TopicActionType.NOTE, status = TopicActionStatus.COMPLETED),
                action(id = 2L, type = TopicActionType.CALENDAR, status = TopicActionStatus.COMPLETED),
                action(id = 3L, type = TopicActionType.REMINDER, status = TopicActionStatus.COMPLETED)
            )
        )

        assertTrue(state.allRequiredCompleted)
        assertEquals("모든 초안이 완료되었습니다.", state.footerMessage)
    }

    private fun action(
        id: Long,
        type: TopicActionType,
        status: TopicActionStatus,
        previewText: String = "본문"
    ): TopicAction {
        return TopicAction(
            id = id,
            topicId = 7L,
            analysisId = 4L,
            type = type,
            status = status,
            targetApp = when (type) {
                TopicActionType.NOTE -> TopicActionTargetApp.SAMSUNG_NOTES
                TopicActionType.CALENDAR -> TopicActionTargetApp.SAMSUNG_CALENDAR
                TopicActionType.REMINDER -> TopicActionTargetApp.SAMSUNG_REMINDER
                TopicActionType.SUMMARY,
                TopicActionType.TODO -> TopicActionTargetApp.NONE
            },
            title = "초안 $id",
            body = "본문 $id",
            previewText = previewText,
            createdAtMillis = id,
            updatedAtMillis = id
        )
    }
}
