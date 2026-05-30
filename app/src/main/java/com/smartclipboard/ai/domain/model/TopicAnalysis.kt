package com.smartclipboard.ai.domain.model

data class TopicAnalysis(
    val id: Long = 0L,
    val topicId: Long,
    val status: TopicAnalysisStatus = TopicAnalysisStatus.PENDING,
    val summary: String? = null,
    val evidence: List<String> = emptyList(),
    val modelName: String? = null,
    val failureReason: String? = null,
    val retryCount: Int = 0,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)

enum class TopicAnalysisStatus {
    PENDING,
    RUNNING,
    DONE,
    FAILED
}
