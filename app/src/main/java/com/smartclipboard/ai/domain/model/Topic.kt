package com.smartclipboard.ai.domain.model

data class Topic(
    val id: Long = 0L,
    val title: String,
    val prompt: String? = null,
    val origin: TopicOrigin,
    val status: TopicStatus = TopicStatus.ACTIVE,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
    val completedAtMillis: Long? = null
) {
    val isCompleted: Boolean
        get() = status == TopicStatus.COMPLETED
}

enum class TopicOrigin {
    USER_REQUEST,
    AI_RECOMMENDATION,
    SYSTEM_SUGGESTION
}

enum class TopicStatus {
    ACTIVE,
    COMPLETED,
    INCOMPLETE,
    ARCHIVED
}

enum class TopicItemSelectedBy {
    USER,
    AI,
    SYSTEM
}
