package com.smartclipboard.ai.presentation.analysis.action

import com.smartclipboard.ai.domain.model.TopicAction
import com.smartclipboard.ai.domain.model.TopicActionStatus
import com.smartclipboard.ai.domain.model.TopicActionTargetApp
import com.smartclipboard.ai.domain.model.TopicActionType
import com.smartclipboard.ai.domain.model.TopicAnalysis
import com.smartclipboard.ai.domain.model.TopicAnalysisStatus
import com.smartclipboard.ai.domain.repository.DataRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class TopicActionDraftUseCase @Inject constructor(
    private val repository: DataRepository
) {
    private var nowMillis: () -> Long = { System.currentTimeMillis() }

    internal constructor(
        repository: DataRepository,
        nowMillis: () -> Long
    ) : this(repository) {
        this.nowMillis = nowMillis
    }

    suspend fun ensureDrafts(topicId: Long): TopicActionDraftResult {
        if (topicId <= 0L) {
            return TopicActionDraftResult.Ignored
        }

        val analysis = repository.observeTopicAnalyses(topicId).first()
            .latestDone()
            ?: return TopicActionDraftResult.Ignored
        val existingActions = repository.observeTopicActions(topicId).first()
        val existingTypes = existingActions.map { it.type }.toSet()
        val missingActions = defaultDrafts(
            topicId = topicId,
            analysis = analysis
        ).filterNot { draft -> draft.type in existingTypes }

        missingActions.forEach { action ->
            repository.saveTopicAction(action)
        }

        return TopicActionDraftResult.Created(count = missingActions.size)
    }

    suspend fun completeAction(action: TopicAction) {
        val now = nowMillis()
        repository.updateTopicAction(
            action.copy(
                status = TopicActionStatus.COMPLETED,
                updatedAtMillis = now,
                completedAtMillis = now
            )
        )
    }

    suspend fun markAllCompleted(topicId: Long) {
        val actions = repository.observeTopicActions(topicId).first()
        actions
            .filterNot { it.status == TopicActionStatus.COMPLETED }
            .forEach { action -> completeAction(action) }
    }

    suspend fun updateActionContent(
        action: TopicAction,
        title: String,
        body: String
    ) {
        val cleanedTitle = title.trim().ifBlank { action.title }
        val cleanedBody = body.trim().ifBlank { action.body }
        repository.updateTopicAction(
            action.copy(
                title = cleanedTitle,
                body = cleanedBody,
                previewText = cleanedBody.toPreviewText(),
                status = TopicActionStatus.PENDING_REVIEW,
                updatedAtMillis = nowMillis()
            )
        )
    }

    private fun defaultDrafts(
        topicId: Long,
        analysis: TopicAnalysis
    ): List<TopicAction> {
        val now = nowMillis()
        val summary = analysis.summary.orEmpty().ifBlank { "분석 요약이 준비되었습니다." }
        val evidenceText = analysis.evidence.takeIf { it.isNotEmpty() }
            ?.joinToString(separator = "\n", prefix = "\n\n사용된 자료\n")
            .orEmpty()
        val noteBody = summary + evidenceText

        return listOf(
            TopicAction(
                topicId = topicId,
                analysisId = analysis.id,
                type = TopicActionType.NOTE,
                status = TopicActionStatus.PENDING_REVIEW,
                targetApp = TopicActionTargetApp.SAMSUNG_NOTES,
                title = "요약 노트",
                body = noteBody,
                previewText = noteBody.toPreviewText(),
                createdAtMillis = now,
                updatedAtMillis = now
            ),
            TopicAction(
                topicId = topicId,
                analysisId = analysis.id,
                type = TopicActionType.CALENDAR,
                status = TopicActionStatus.PENDING_REVIEW,
                targetApp = TopicActionTargetApp.SAMSUNG_CALENDAR,
                title = "일정 초안",
                body = summary,
                previewText = "일정으로 보낼 내용을 확인하세요.",
                createdAtMillis = now,
                updatedAtMillis = now
            ),
            TopicAction(
                topicId = topicId,
                analysisId = analysis.id,
                type = TopicActionType.REMINDER,
                status = TopicActionStatus.PENDING_REVIEW,
                targetApp = TopicActionTargetApp.SAMSUNG_REMINDER,
                title = "리마인더 초안",
                body = summary,
                previewText = "리마인더로 보낼 내용을 확인하세요.",
                createdAtMillis = now,
                updatedAtMillis = now
            )
        )
    }

    private fun List<TopicAnalysis>.latestDone(): TopicAnalysis? {
        return filter { it.status == TopicAnalysisStatus.DONE }
            .maxWithOrNull(compareBy<TopicAnalysis> { it.createdAtMillis }.thenBy { it.id })
    }

    private fun String.toPreviewText(): String {
        return replace(Regex("\\s+"), " ")
            .trim()
            .take(MAX_PREVIEW_LENGTH)
    }

    private companion object {
        const val MAX_PREVIEW_LENGTH = 120
    }
}

sealed interface TopicActionDraftResult {
    data class Created(val count: Int) : TopicActionDraftResult
    data object Ignored : TopicActionDraftResult
}
