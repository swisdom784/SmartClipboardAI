package com.smartclipboard.ai.presentation.logs

import com.smartclipboard.ai.domain.model.Topic
import com.smartclipboard.ai.domain.model.TopicOrigin
import com.smartclipboard.ai.domain.model.TopicStatus

object LogsUiStateMapper {
    fun map(
        topics: List<Topic>,
        selectedFilter: LogFilterId
    ): LogsUiState {
        val entries = topics
            .filterNot { it.status == TopicStatus.ARCHIVED }
            .map { it.toLogEntryItem() }
            .sortedWith(compareByDescending<LogEntryItem> { it.updatedAtMillis }.thenByDescending { it.id })

        return LogsUiState(
            filters = LogFilterId.entries.map { filter ->
                LogFilterItem(
                    id = filter,
                    label = filter.label,
                    count = entries.count { entry -> entry.matches(filter) },
                    isSelected = filter == selectedFilter
                )
            },
            selectedFilter = selectedFilter,
            visibleEntries = entries.filter { entry -> entry.matches(selectedFilter) }
        )
    }

    private fun Topic.toLogEntryItem(): LogEntryItem {
        val sourceBadge = when (origin) {
            TopicOrigin.USER_REQUEST -> LogBadge.USER_REQUEST
            TopicOrigin.AI_RECOMMENDATION,
            TopicOrigin.SYSTEM_SUGGESTION -> LogBadge.AI_RECOMMENDATION
        }
        val statusBadge = when (status) {
            TopicStatus.COMPLETED -> LogBadge.COMPLETED
            TopicStatus.INCOMPLETE -> LogBadge.INCOMPLETE
            TopicStatus.ACTIVE,
            TopicStatus.ARCHIVED -> LogBadge.IN_PROGRESS
        }

        return LogEntryItem(
            id = id,
            title = title,
            subtitle = prompt.orEmpty(),
            updatedAtMillis = updatedAtMillis,
            badges = listOf(sourceBadge, statusBadge)
        )
    }

    private fun LogEntryItem.matches(filter: LogFilterId): Boolean {
        return when (filter) {
            LogFilterId.ALL -> true
            LogFilterId.USER_REQUEST -> badges.contains(LogBadge.USER_REQUEST)
            LogFilterId.AI_RECOMMENDATION -> badges.contains(LogBadge.AI_RECOMMENDATION)
            LogFilterId.IN_PROGRESS -> badges.contains(LogBadge.IN_PROGRESS)
            LogFilterId.COMPLETED -> badges.contains(LogBadge.COMPLETED)
            LogFilterId.INCOMPLETE -> badges.contains(LogBadge.INCOMPLETE)
        }
    }
}
