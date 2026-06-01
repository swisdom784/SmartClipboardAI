package com.smartclipboard.ai.processing.gemini.analysis

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.Topic

interface TopicAnalysisGenerator {
    suspend fun generate(input: TopicAnalysisInput): TopicAnalysisDraft
}

data class TopicAnalysisInput(
    val topic: Topic,
    val selectedItems: List<DataItem>
)

data class TopicAnalysisDraft(
    val summary: String,
    val evidence: List<String>,
    val modelName: String
)
