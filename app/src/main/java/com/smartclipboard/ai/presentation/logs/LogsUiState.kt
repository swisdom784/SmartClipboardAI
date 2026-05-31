package com.smartclipboard.ai.presentation.logs

data class LogsUiState(
    val filters: List<LogFilterItem> = emptyList(),
    val selectedFilter: LogFilterId = LogFilterId.ALL,
    val visibleEntries: List<LogEntryItem> = emptyList()
)

data class LogFilterItem(
    val id: LogFilterId,
    val label: String,
    val count: Int,
    val isSelected: Boolean
)

enum class LogFilterId(val label: String) {
    ALL("전체"),
    USER_REQUEST("사용자 요청"),
    AI_RECOMMENDATION("AI 추천"),
    IN_PROGRESS("진행 중"),
    COMPLETED("완료"),
    INCOMPLETE("미완료")
}

data class LogEntryItem(
    val id: Long,
    val title: String,
    val subtitle: String,
    val updatedAtMillis: Long,
    val badges: List<LogBadge>
)

enum class LogBadge(val label: String) {
    USER_REQUEST("사용자 요청"),
    AI_RECOMMENDATION("AI 추천"),
    IN_PROGRESS("진행 중"),
    COMPLETED("완료"),
    INCOMPLETE("미완료")
}
