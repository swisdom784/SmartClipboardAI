package com.smartclipboard.ai.processing.gemini.recommendation

import com.smartclipboard.ai.domain.model.DataItem

interface TopicRecommendationGenerator {
    suspend fun generate(items: List<DataItem>): List<TopicRecommendationCandidate>
}
