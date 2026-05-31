package com.smartclipboard.ai.presentation.logs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartclipboard.ai.ui.theme.AppOutline
import com.smartclipboard.ai.ui.theme.AppSecondaryText
import com.smartclipboard.ai.ui.theme.BlueSoft
import com.smartclipboard.ai.ui.theme.SamsungBlue
import com.smartclipboard.ai.ui.theme.SmartClipboardTheme

@Composable
fun LogsRoute(
    modifier: Modifier = Modifier,
    viewModel: LogsViewModel = hiltViewModel(),
    onOpenTopic: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LogsScreen(
        state = uiState,
        modifier = modifier,
        onFilterSelected = viewModel::selectFilter,
        onOpenTopic = onOpenTopic
    )
}

@Composable
fun LogsScreen(
    state: LogsUiState,
    modifier: Modifier = Modifier,
    onFilterSelected: (LogFilterId) -> Unit = {},
    onOpenTopic: (Long) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        LogsHeader()
        LogsFilterRail(
            filters = state.filters,
            onFilterSelected = onFilterSelected
        )
        LogsEntryList(
            entries = state.visibleEntries,
            onOpenTopic = onOpenTopic
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun LogsHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "기록",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "확인한 작업",
            style = MaterialTheme.typography.bodyMedium,
            color = AppSecondaryText
        )
    }
}

@Composable
private fun LogsFilterRail(
    filters: List<LogFilterItem>,
    onFilterSelected: (LogFilterId) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            Text(
                text = "${filter.label} ${filter.count}",
                modifier = Modifier
                    .heightIn(min = 36.dp)
                    .background(
                        color = if (filter.isSelected) BlueSoft else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (filter.isSelected) SamsungBlue.copy(alpha = 0.28f) else AppOutline,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onFilterSelected(filter.id) }
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                style = MaterialTheme.typography.labelMedium,
                color = if (filter.isSelected) SamsungBlue else AppSecondaryText,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun LogsEntryList(
    entries: List<LogEntryItem>,
    onOpenTopic: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (entries.isEmpty()) {
            EmptyLogRow()
        } else {
            entries.forEach { entry ->
                LogEntryCard(
                    entry = entry,
                    onClick = { onOpenTopic(entry.id) }
                )
            }
        }
    }
}

@Composable
private fun EmptyLogRow() {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, AppOutline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "표시할 기록이 없습니다.",
            modifier = Modifier.padding(14.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = AppSecondaryText
        )
    }
}

@Composable
private fun LogEntryCard(
    entry: LogEntryItem,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, AppOutline),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (entry.subtitle.isNotBlank()) {
                Text(
                    text = entry.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppSecondaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                entry.badges.forEach { badge ->
                    LogBadgePill(badge = badge)
                }
            }
        }
    }
}

@Composable
private fun LogBadgePill(badge: LogBadge) {
    Text(
        text = badge.label,
        modifier = Modifier
            .background(
                color = if (badge == LogBadge.COMPLETED) BlueSoft else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = if (badge == LogBadge.COMPLETED) SamsungBlue.copy(alpha = 0.28f) else AppOutline,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelSmall,
        color = if (badge == LogBadge.COMPLETED) SamsungBlue else AppSecondaryText,
        maxLines = 1
    )
}

@Preview(showBackground = true)
@Composable
private fun LogsScreenPreview() {
    SmartClipboardTheme {
        LogsScreen(
            state = LogsUiState(
                filters = LogFilterId.entries.map { filter ->
                    LogFilterItem(
                        id = filter,
                        label = filter.label,
                        count = 2,
                        isSelected = filter == LogFilterId.ALL
                    )
                },
                visibleEntries = listOf(
                    LogEntryItem(
                        id = 1L,
                        title = "출장 준비",
                        subtitle = "항공권과 숙소 자료",
                        updatedAtMillis = 1L,
                        badges = listOf(LogBadge.USER_REQUEST, LogBadge.IN_PROGRESS)
                    ),
                    LogEntryItem(
                        id = 2L,
                        title = "회의 요약",
                        subtitle = "팀 미팅 기록 정리",
                        updatedAtMillis = 2L,
                        badges = listOf(LogBadge.AI_RECOMMENDATION, LogBadge.COMPLETED)
                    )
                )
            )
        )
    }
}
