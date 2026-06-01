package com.smartclipboard.ai.presentation.analysis

import com.smartclipboard.ai.domain.model.TopicAnalysis
import com.smartclipboard.ai.domain.model.TopicAnalysisStatus

data class TopicAnalysisUiState(
    val topicId: Long = 0L,
    val statusLabel: String = "분석 준비",
    val summary: String = "선택한 자료를 분석합니다.",
    val evidence: List<String> = emptyList(),
    val isRunning: Boolean = false,
    val canRetry: Boolean = false,
    val isDone: Boolean = false
)

object TopicAnalysisUiStateMapper {
    fun map(
        topicId: Long,
        analyses: List<TopicAnalysis>
    ): TopicAnalysisUiState {
        val latest = analyses.maxWithOrNull(
            compareBy<TopicAnalysis> { it.createdAtMillis }.thenBy { it.id }
        ) ?: return TopicAnalysisUiState(topicId = topicId)

        return when (latest.status) {
            TopicAnalysisStatus.PENDING -> TopicAnalysisUiState(
                topicId = topicId,
                statusLabel = "분석 준비",
                summary = "선택한 자료를 분석합니다."
            )
            TopicAnalysisStatus.RUNNING -> TopicAnalysisUiState(
                topicId = topicId,
                statusLabel = "분석 중",
                summary = "자료를 읽고 초안을 만드는 중입니다.",
                isRunning = true
            )
            TopicAnalysisStatus.DONE -> TopicAnalysisUiState(
                topicId = topicId,
                statusLabel = "분석 완료",
                summary = latest.summary.orEmpty(),
                evidence = latest.evidence,
                isDone = true
            )
            TopicAnalysisStatus.FAILED -> TopicAnalysisUiState(
                topicId = topicId,
                statusLabel = "분석 보류",
                summary = latest.failureReason ?: "분석을 완료하지 못했습니다.",
                canRetry = true
            )
        }
    }
}
