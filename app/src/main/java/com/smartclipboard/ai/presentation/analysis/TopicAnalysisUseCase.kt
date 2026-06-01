package com.smartclipboard.ai.presentation.analysis

import com.smartclipboard.ai.domain.model.TopicAnalysis
import com.smartclipboard.ai.domain.model.TopicAnalysisStatus
import com.smartclipboard.ai.domain.repository.DataRepository
import com.smartclipboard.ai.processing.gemini.analysis.TopicAnalysisGenerator
import com.smartclipboard.ai.processing.gemini.analysis.TopicAnalysisInput
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class TopicAnalysisUseCase @Inject constructor(
    private val repository: DataRepository,
    private val generator: TopicAnalysisGenerator
) {
    private var nowMillis: () -> Long = { System.currentTimeMillis() }

    internal constructor(
        repository: DataRepository,
        generator: TopicAnalysisGenerator,
        nowMillis: () -> Long
    ) : this(repository, generator) {
        this.nowMillis = nowMillis
    }

    suspend fun generate(topicId: Long): TopicAnalysisGenerationResult {
        if (topicId <= 0L) {
            return TopicAnalysisGenerationResult.Ignored
        }

        val topic = repository.observeTopics().first()
            .firstOrNull { it.id == topicId }
            ?: return TopicAnalysisGenerationResult.Ignored
        val selectedItems = repository.observeDataItemsForTopic(topicId).first()
        val createdAtMillis = nowMillis()
        val runningId = repository.saveTopicAnalysis(
            TopicAnalysis(
                topicId = topicId,
                status = TopicAnalysisStatus.RUNNING,
                createdAtMillis = createdAtMillis,
                updatedAtMillis = createdAtMillis
            )
        )

        return runCatching {
            generator.generate(
                TopicAnalysisInput(
                    topic = topic,
                    selectedItems = selectedItems
                )
            )
        }.fold(
            onSuccess = { draft ->
                repository.saveTopicAnalysis(
                    TopicAnalysis(
                        id = runningId,
                        topicId = topicId,
                        status = TopicAnalysisStatus.DONE,
                        summary = draft.summary,
                        evidence = draft.evidence,
                        modelName = draft.modelName,
                        createdAtMillis = createdAtMillis,
                        updatedAtMillis = nowMillis()
                    )
                )
                TopicAnalysisGenerationResult.Generated(analysisId = runningId)
            },
            onFailure = { throwable ->
                repository.saveTopicAnalysis(
                    TopicAnalysis(
                        id = runningId,
                        topicId = topicId,
                        status = TopicAnalysisStatus.FAILED,
                        failureReason = throwable.message ?: "분석에 실패했습니다.",
                        retryCount = 1,
                        createdAtMillis = createdAtMillis,
                        updatedAtMillis = nowMillis()
                    )
                )
                TopicAnalysisGenerationResult.Failed(
                    analysisId = runningId,
                    reason = throwable.message ?: "분석에 실패했습니다."
                )
            }
        )
    }
}

sealed interface TopicAnalysisGenerationResult {
    data class Generated(val analysisId: Long) : TopicAnalysisGenerationResult
    data class Failed(val analysisId: Long, val reason: String) : TopicAnalysisGenerationResult
    data object Ignored : TopicAnalysisGenerationResult
}
