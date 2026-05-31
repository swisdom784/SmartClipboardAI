package com.smartclipboard.ai.domain.repository

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.Topic
import com.smartclipboard.ai.processing.gemini.recommendation.RecommendationSession

data class HomeRepositoryState(
    val recentDataItems: List<DataItem>,
    val activeTopics: List<Topic>,
    val recommendationSession: RecommendationSession?
)

data class InboxFilter(
    val types: Set<DataItemType> = emptySet(),
    val importantOnly: Boolean = false,
    val pendingAnalysisOnly: Boolean = false
)
