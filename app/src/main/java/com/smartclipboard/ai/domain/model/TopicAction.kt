package com.smartclipboard.ai.domain.model

data class TopicAction(
    val id: Long = 0L,
    val topicId: Long,
    val analysisId: Long? = null,
    val type: TopicActionType,
    val status: TopicActionStatus = TopicActionStatus.PENDING_REVIEW,
    val targetApp: TopicActionTargetApp = TopicActionTargetApp.NONE,
    val title: String,
    val body: String,
    val previewText: String,
    val scheduledStartAtMillis: Long? = null,
    val scheduledEndAtMillis: Long? = null,
    val isAllDay: Boolean = false,
    val location: String? = null,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
    val completedAtMillis: Long? = null
)

enum class TopicActionType {
    SUMMARY,
    NOTE,
    CALENDAR,
    REMINDER,
    TODO
}

enum class TopicActionStatus {
    PENDING_REVIEW,
    IN_PROGRESS,
    COMPLETED,
    INCOMPLETE,
    EXPORTED,
    DISMISSED
}

enum class TopicActionTargetApp {
    NONE,
    SAMSUNG_NOTES,
    SAMSUNG_CALENDAR,
    SAMSUNG_REMINDER,
    SYSTEM_CALENDAR
}
