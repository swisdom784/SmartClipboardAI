package com.smartclipboard.ai.data.source.local

import com.smartclipboard.ai.data.source.local.mapper.toDomain
import com.smartclipboard.ai.data.source.local.mapper.toEntity
import com.smartclipboard.ai.domain.model.TopicAction
import com.smartclipboard.ai.domain.model.TopicActionStatus
import com.smartclipboard.ai.domain.model.TopicActionTargetApp
import com.smartclipboard.ai.domain.model.TopicActionType
import org.junit.Assert.assertEquals
import org.junit.Test

class TopicActionEntityMapperTest {
    @Test
    fun calendarActionRoundTripPreservesReviewablePayload() {
        val action = TopicAction(
            id = 11L,
            topicId = 3L,
            analysisId = 5L,
            type = TopicActionType.CALENDAR,
            status = TopicActionStatus.PENDING_REVIEW,
            targetApp = TopicActionTargetApp.SAMSUNG_CALENDAR,
            title = "제주도 여행",
            body = "항공권과 숙소 예약 내용을 확인합니다.",
            previewText = "5월 1일부터 5월 5일까지 제주도 여행",
            scheduledStartAtMillis = 1_714_492_800_000L,
            scheduledEndAtMillis = 1_714_838_399_000L,
            isAllDay = true,
            location = "제주 국제공항",
            createdAtMillis = 100L,
            updatedAtMillis = 200L,
            completedAtMillis = null
        )

        val restored = action.toEntity().toDomain()

        assertEquals(action, restored)
    }

    @Test
    fun reminderActionSupportsIncompleteAndCompletedStates() {
        val incomplete = TopicAction(
            id = 12L,
            topicId = 3L,
            analysisId = null,
            type = TopicActionType.REMINDER,
            status = TopicActionStatus.INCOMPLETE,
            targetApp = TopicActionTargetApp.SAMSUNG_REMINDER,
            title = "여권 챙기기",
            body = "출발 전날 저녁에 확인",
            previewText = "여권 챙기기",
            createdAtMillis = 300L,
            updatedAtMillis = 400L
        )
        val completed = incomplete.copy(
            status = TopicActionStatus.COMPLETED,
            completedAtMillis = 500L
        )

        assertEquals(TopicActionStatus.INCOMPLETE, incomplete.toEntity().toDomain().status)
        assertEquals(TopicActionStatus.COMPLETED, completed.toEntity().toDomain().status)
        assertEquals(500L, completed.toEntity().toDomain().completedAtMillis)
    }
}
