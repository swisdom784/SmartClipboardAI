package com.smartclipboard.ai.processing.gemini.recommendation

data class TopicRecommendationCandidate(
    val id: String,
    val title: String,
    val reason: String,
    val prompt: String,
    val sourceDataItemIds: List<Long>,
    val createdAtMillis: Long,
    val confidence: Double? = null,
    val badge: RecommendationBadge = RecommendationBadge.AI_RECOMMENDATION
)

enum class RecommendationBadge {
    AI_RECOMMENDATION
}
