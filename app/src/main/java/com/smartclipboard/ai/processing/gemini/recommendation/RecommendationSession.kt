package com.smartclipboard.ai.processing.gemini.recommendation

data class RecommendationSession(
    val id: String,
    val status: RecommendationSessionStatus,
    val recommendations: List<TopicRecommendationCandidate>,
    val createdAtMillis: Long,
    val message: String? = null
)

enum class RecommendationSessionStatus {
    READY,
    SKIPPED,
    FAILED
}
