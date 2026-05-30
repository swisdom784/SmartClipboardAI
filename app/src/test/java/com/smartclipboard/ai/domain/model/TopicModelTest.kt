package com.smartclipboard.ai.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class TopicModelTest {
    @Test
    fun userCreatedTopicStartsActiveAndKeepsOriginBadge() {
        val topic = Topic(
            id = 1L,
            title = "제주 여행 정리",
            prompt = "제주도 여행 자료 정리해줘",
            origin = TopicOrigin.USER_REQUEST,
            createdAtMillis = 100L,
            updatedAtMillis = 100L
        )

        assertEquals(TopicStatus.ACTIVE, topic.status)
        assertEquals(TopicOrigin.USER_REQUEST, topic.origin)
        assertFalse(topic.isCompleted)
    }

    @Test
    fun aiRecommendedTopicCanBeMarkedCompletedWithoutLosingOrigin() {
        val topic = Topic(
            id = 2L,
            title = "최근 자료 정리",
            prompt = null,
            origin = TopicOrigin.AI_RECOMMENDATION,
            status = TopicStatus.COMPLETED,
            createdAtMillis = 100L,
            updatedAtMillis = 200L,
            completedAtMillis = 300L
        )

        assertEquals(TopicOrigin.AI_RECOMMENDATION, topic.origin)
        assertEquals(TopicStatus.COMPLETED, topic.status)
        assertEquals(true, topic.isCompleted)
    }
}
