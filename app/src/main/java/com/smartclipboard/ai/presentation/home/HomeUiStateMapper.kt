package com.smartclipboard.ai.presentation.home

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.EnrichmentStatus
import com.smartclipboard.ai.domain.model.Topic
import com.smartclipboard.ai.domain.model.TopicOrigin
import com.smartclipboard.ai.domain.model.TopicStatus
import com.smartclipboard.ai.domain.repository.HomeRepositoryState
import com.smartclipboard.ai.processing.gemini.recommendation.RecommendationSession
import com.smartclipboard.ai.processing.gemini.recommendation.RecommendationSessionStatus
import com.smartclipboard.ai.processing.gemini.recommendation.TopicRecommendationCandidate

object HomeUiStateMapper {
    fun map(state: HomeRepositoryState): HomeUiState {
        val recommendationTasks = state.recommendationSession
            .readyRecommendations()
            .map { candidate -> candidate.toTaskItem() }

        val topicTasks = state.activeTopics.map { topic -> topic.toTaskItem() }
        val materials = state.recentDataItems.map { item -> item.toMaterialItem() }

        return HomeUiState(
            tasks = recommendationTasks + topicTasks,
            collectionSummary = state.recentDataItems.toCollectionSummary(),
            recentMaterials = materials
        )
    }

    private fun RecommendationSession?.readyRecommendations(): List<TopicRecommendationCandidate> {
        return if (this?.status == RecommendationSessionStatus.READY) {
            recommendations
        } else {
            emptyList()
        }
    }

    private fun TopicRecommendationCandidate.toTaskItem(): HomeTaskItem {
        return HomeTaskItem(
            id = "recommendation:$id",
            title = title,
            subtitle = reason,
            prompt = prompt,
            kind = HomeTaskKind.RECOMMENDATION,
            badges = listOf(HomeTaskBadge.AI_RECOMMENDATION, HomeTaskBadge.REVIEW_REQUIRED),
            sourceDataItemIds = sourceDataItemIds
        )
    }

    private fun Topic.toTaskItem(): HomeTaskItem {
        val sourceBadge = when (origin) {
            TopicOrigin.USER_REQUEST -> HomeTaskBadge.USER_REQUEST
            TopicOrigin.AI_RECOMMENDATION,
            TopicOrigin.SYSTEM_SUGGESTION -> HomeTaskBadge.AI_RECOMMENDATION
        }
        val statusBadge = when (status) {
            TopicStatus.COMPLETED -> HomeTaskBadge.COMPLETED
            TopicStatus.ACTIVE,
            TopicStatus.INCOMPLETE,
            TopicStatus.ARCHIVED -> HomeTaskBadge.IN_PROGRESS
        }

        return HomeTaskItem(
            id = "topic:$id",
            title = title,
            subtitle = prompt.orEmpty(),
            prompt = prompt,
            kind = HomeTaskKind.TOPIC,
            badges = listOf(sourceBadge, statusBadge)
        )
    }

    private fun List<DataItem>.toCollectionSummary(): HomeCollectionSummary {
        if (isEmpty()) {
            return HomeCollectionSummary()
        }

        val analysisActiveCount = count { item ->
            item.enrichment.status == EnrichmentStatus.PENDING ||
                item.enrichment.status == EnrichmentStatus.PROCESSING
        }
        val title = if (analysisActiveCount > 0) {
            "자료 ${size}개 · 분석 중 ${analysisActiveCount}개"
        } else {
            "자료 ${size}개 · 정리 완료"
        }

        val countsByType = groupingBy { it.type.toHomeMaterialType() }.eachCount()
        val subtitle = listOfNotNull(
            countsByType.format(HomeMaterialType.IMAGE),
            countsByType.format(HomeMaterialType.LINK),
            countsByType.format(HomeMaterialType.TEXT),
            countsByType.format(HomeMaterialType.FILE)
        ).joinToString(" · ")

        return HomeCollectionSummary(
            title = title,
            subtitle = subtitle.ifBlank { "새 자료가 들어오면 여기에 정리됩니다." }
        )
    }

    private fun Map<HomeMaterialType, Int>.format(type: HomeMaterialType): String? {
        val count = this[type] ?: return null
        return "${type.label} $count"
    }

    private fun DataItem.toMaterialItem(): HomeMaterialItem {
        val type = type.toHomeMaterialType()
        val title = when {
            displayName != null -> displayName
            type == HomeMaterialType.LINK -> enrichment.ogTitle ?: textContent ?: "링크"
            type == HomeMaterialType.TEXT -> textContent ?: "텍스트"
            else -> type.label
        }
        val subtitle = when (enrichment.status) {
            EnrichmentStatus.DONE -> "분석 완료"
            EnrichmentStatus.FAILED -> "분석 보류"
            EnrichmentStatus.PROCESSING -> "분석 중"
            EnrichmentStatus.PENDING -> "분석 대기"
        }

        return HomeMaterialItem(
            id = id,
            title = title,
            subtitle = subtitle,
            type = type,
            isAnalysisPending = enrichment.status == EnrichmentStatus.PENDING ||
                enrichment.status == EnrichmentStatus.PROCESSING
        )
    }

    private fun DataItemType.toHomeMaterialType(): HomeMaterialType {
        return when (this) {
            DataItemType.IMAGE,
            DataItemType.SCREENSHOT,
            DataItemType.DOWNLOAD_IMAGE -> HomeMaterialType.IMAGE
            DataItemType.LINK -> HomeMaterialType.LINK
            DataItemType.TEXT -> HomeMaterialType.TEXT
            DataItemType.FILE -> HomeMaterialType.FILE
        }
    }
}
