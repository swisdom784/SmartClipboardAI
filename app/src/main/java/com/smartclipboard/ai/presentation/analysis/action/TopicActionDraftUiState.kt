package com.smartclipboard.ai.presentation.analysis.action

import com.smartclipboard.ai.domain.model.TopicAction
import com.smartclipboard.ai.domain.model.TopicActionStatus
import com.smartclipboard.ai.domain.model.TopicActionType

data class TopicActionDraftUiState(
    val cards: List<TopicActionCardUiState> = emptyList(),
    val allRequiredCompleted: Boolean = false,
    val footerMessage: String = "필요한 초안을 확인하고 완료하세요."
)

data class TopicActionCardUiState(
    val id: Long,
    val type: TopicActionType,
    val typeLabel: String,
    val statusLabel: String,
    val title: String,
    val body: String,
    val previewText: String,
    val canExportToNotes: Boolean,
    val canExportToCalendar: Boolean,
    val canExportToReminder: Boolean,
    val scheduledStartAtMillis: Long?,
    val scheduledEndAtMillis: Long?,
    val isAllDay: Boolean,
    val location: String?,
    val isCollapsed: Boolean,
    val isCompleted: Boolean
)

object TopicActionDraftUiStateMapper {
    fun map(actions: List<TopicAction>): TopicActionDraftUiState {
        val sorted = actions.sortedWith(
            compareBy<TopicAction> { it.type.sortOrder() }
                .thenBy { it.createdAtMillis }
                .thenBy { it.id }
        )
        val cards = sorted.map { action -> action.toCardUiState() }
        val completedTypes = sorted
            .filter { it.status.isDoneStatus() }
            .map { it.type }
            .toSet()
        val allRequiredCompleted = REQUIRED_TYPES.all { it in completedTypes }

        return TopicActionDraftUiState(
            cards = cards,
            allRequiredCompleted = allRequiredCompleted,
            footerMessage = if (allRequiredCompleted) {
                "모든 초안이 완료되었습니다."
            } else {
                "필요한 초안을 확인하고 완료하세요."
            }
        )
    }

    private fun TopicAction.toCardUiState(): TopicActionCardUiState {
        val isCompleted = status.isDoneStatus()
        return TopicActionCardUiState(
            id = id,
            type = type,
            typeLabel = type.label(),
            statusLabel = status.label(),
            title = title,
            body = body,
            previewText = previewText.compactPreview(),
            canExportToNotes = type == TopicActionType.NOTE && !isCompleted,
            canExportToCalendar = type == TopicActionType.CALENDAR && !isCompleted,
            canExportToReminder = type == TopicActionType.REMINDER && !isCompleted,
            scheduledStartAtMillis = scheduledStartAtMillis,
            scheduledEndAtMillis = scheduledEndAtMillis,
            isAllDay = isAllDay,
            location = location,
            isCollapsed = isCompleted,
            isCompleted = isCompleted
        )
    }

    private fun TopicActionType.label(): String {
        return when (this) {
            TopicActionType.SUMMARY -> "요약"
            TopicActionType.NOTE -> "노트"
            TopicActionType.CALENDAR -> "캘린더"
            TopicActionType.REMINDER -> "리마인더"
            TopicActionType.TODO -> "할 일"
        }
    }

    private fun TopicActionType.sortOrder(): Int {
        return when (this) {
            TopicActionType.NOTE -> 0
            TopicActionType.CALENDAR -> 1
            TopicActionType.REMINDER -> 2
            TopicActionType.TODO -> 3
            TopicActionType.SUMMARY -> 4
        }
    }

    private fun TopicActionStatus.label(): String {
        return when (this) {
            TopicActionStatus.PENDING_REVIEW -> "검토 필요"
            TopicActionStatus.IN_PROGRESS -> "진행 중"
            TopicActionStatus.COMPLETED -> "완료"
            TopicActionStatus.INCOMPLETE -> "미완료"
            TopicActionStatus.EXPORTED -> "전송됨"
            TopicActionStatus.DISMISSED -> "보류"
        }
    }

    private fun TopicActionStatus.isDoneStatus(): Boolean {
        return this == TopicActionStatus.COMPLETED || this == TopicActionStatus.EXPORTED
    }

    private fun String.compactPreview(): String {
        val compact = replace(Regex("\\s+"), " ").trim()
        return if (compact.length <= MAX_PREVIEW_LENGTH) {
            compact
        } else {
            compact.take(MAX_PREVIEW_LENGTH - 3) + "..."
        }
    }

    private val REQUIRED_TYPES = setOf(
        TopicActionType.NOTE,
        TopicActionType.CALENDAR,
        TopicActionType.REMINDER
    )
    private const val MAX_PREVIEW_LENGTH = 80
}
