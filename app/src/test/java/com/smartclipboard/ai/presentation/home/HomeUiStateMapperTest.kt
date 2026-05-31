package com.smartclipboard.ai.presentation.home

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemEnrichment
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.EnrichmentStatus
import com.smartclipboard.ai.domain.model.Topic
import com.smartclipboard.ai.domain.model.TopicOrigin
import com.smartclipboard.ai.domain.model.TopicStatus
import com.smartclipboard.ai.domain.repository.HomeRepositoryState
import com.smartclipboard.ai.processing.gemini.recommendation.RecommendationSession
import com.smartclipboard.ai.processing.gemini.recommendation.RecommendationSessionStatus
import com.smartclipboard.ai.processing.gemini.recommendation.TopicRecommendationCandidate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeUiStateMapperTest {
    @Test
    fun mapsActiveTopicsAndCurrentRecommendationsIntoTaskCards() {
        val now = 1_000L
        val state = HomeUiStateMapper.map(
            HomeRepositoryState(
                recentDataItems = emptyList(),
                activeTopics = listOf(
                    topic(
                        id = 10L,
                        title = "출장 자료 정리",
                        origin = TopicOrigin.USER_REQUEST,
                        status = TopicStatus.ACTIVE
                    ),
                    topic(
                        id = 11L,
                        title = "회의 내용 요약",
                        origin = TopicOrigin.AI_RECOMMENDATION,
                        status = TopicStatus.COMPLETED
                    )
                ),
                recommendationSession = RecommendationSession(
                    id = "session-1",
                    status = RecommendationSessionStatus.READY,
                    recommendations = listOf(
                        TopicRecommendationCandidate(
                            id = "rec-1",
                            title = "최근 자료 정리",
                            reason = "새 이미지와 링크가 함께 들어왔습니다.",
                            prompt = "최근 자료를 하나의 작업으로 정리해줘",
                            sourceDataItemIds = listOf(1L, 2L),
                            createdAtMillis = now
                        )
                    ),
                    createdAtMillis = now
                )
            )
        )

        assertEquals("무엇을 정리할까요?", state.inputPlaceholder)
        assertEquals(3, state.tasks.size)

        assertEquals("최근 자료 정리", state.tasks[0].title)
        assertEquals(HomeTaskKind.RECOMMENDATION, state.tasks[0].kind)
        assertTrue(state.tasks[0].badges.contains(HomeTaskBadge.AI_RECOMMENDATION))
        assertTrue(state.tasks[0].badges.contains(HomeTaskBadge.REVIEW_REQUIRED))

        assertEquals("출장 자료 정리", state.tasks[1].title)
        assertTrue(state.tasks[1].badges.contains(HomeTaskBadge.USER_REQUEST))
        assertTrue(state.tasks[1].badges.contains(HomeTaskBadge.IN_PROGRESS))

        assertEquals("회의 내용 요약", state.tasks[2].title)
        assertTrue(state.tasks[2].badges.contains(HomeTaskBadge.AI_RECOMMENDATION))
        assertTrue(state.tasks[2].badges.contains(HomeTaskBadge.COMPLETED))
    }

    @Test
    fun summarizesRecentMaterialsByTypeAndAnalysisStatus() {
        val state = HomeUiStateMapper.map(
            HomeRepositoryState(
                recentDataItems = listOf(
                    item(id = 1L, type = DataItemType.SCREENSHOT, status = EnrichmentStatus.DONE),
                    item(id = 2L, type = DataItemType.IMAGE, status = EnrichmentStatus.PENDING),
                    item(id = 3L, type = DataItemType.LINK, status = EnrichmentStatus.FAILED),
                    item(id = 4L, type = DataItemType.TEXT, status = EnrichmentStatus.PROCESSING)
                ),
                activeTopics = emptyList(),
                recommendationSession = RecommendationSession(
                    id = "skipped",
                    status = RecommendationSessionStatus.SKIPPED,
                    recommendations = listOf(
                        TopicRecommendationCandidate(
                            id = "old",
                            title = "표시되면 안 됨",
                            reason = "추천 세션이 준비 상태가 아닙니다.",
                            prompt = "표시되면 안 됨",
                            sourceDataItemIds = emptyList(),
                            createdAtMillis = 1L
                        )
                    ),
                    createdAtMillis = 1L
                )
            )
        )

        assertFalse(state.hasReviewableRecommendations)
        assertEquals("자료 4개 · 분석 중 2개", state.collectionSummary.title)
        assertEquals("이미지 2 · 링크 1 · 텍스트 1", state.collectionSummary.subtitle)
        assertEquals(4, state.recentMaterials.size)
        assertEquals(HomeMaterialType.IMAGE, state.recentMaterials[0].type)
        assertEquals(HomeMaterialType.LINK, state.recentMaterials[2].type)
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
            prompt = null,
            origin = origin,
            status = status,
            createdAtMillis = id,
            updatedAtMillis = id,
            completedAtMillis = if (status == TopicStatus.COMPLETED) id else null
        )
    }

    private fun item(
        id: Long,
        type: DataItemType,
        status: EnrichmentStatus
    ): DataItem {
        return DataItem(
            id = id,
            type = type,
            source = DataItemSource.MEDIASTORE,
            textContent = if (type == DataItemType.TEXT) "복사한 텍스트" else null,
            sourceUri = null,
            capturedAtMillis = id,
            createdAtMillis = id,
            updatedAtMillis = id,
            enrichment = DataItemEnrichment(status = status),
            displayName = "item-$id"
        )
    }
}
