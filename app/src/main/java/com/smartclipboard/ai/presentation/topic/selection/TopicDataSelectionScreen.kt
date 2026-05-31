package com.smartclipboard.ai.presentation.topic.selection

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun TopicDataSelectionRoute(
    topicId: Long,
    modifier: Modifier = Modifier,
    viewModel: TopicDataSelectionViewModel = hiltViewModel(),
    onClose: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(topicId) {
        viewModel.load(topicId)
    }
    LaunchedEffect(viewModel) {
        viewModel.selectionSavedEvents.collect {
            onClose()
        }
    }

    TopicDataSelectionScreen(
        state = uiState,
        modifier = modifier,
        onToggleItem = viewModel::toggleItem,
        onSave = viewModel::saveSelection,
        onClose = onClose
    )
}

@Composable
fun TopicDataSelectionScreen(
    state: TopicDataSelectionUiState,
    modifier: Modifier = Modifier,
    onToggleItem: (Long) -> Unit = {},
    onSave: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopicDataSelectionHeader(onClose = onClose)
        TopicDataSelectionSummaryCard(summary = state.summary)
        TopicSelectableItemsSection(
            items = state.items,
            onToggleItem = onToggleItem
        )
        Button(
            onClick = onSave,
            enabled = !state.isSaving && state.topicId > 0L,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state.isSaving) "저장 중" else "선택 저장")
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun TopicDataSelectionHeader(onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "자료 선택",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "AI 초안에 사용할 자료를 확인합니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = AppSecondaryText
            )
        }
        TextButton(onClick = onClose) {
            Text("닫기")
        }
    }
}

@Composable
private fun TopicDataSelectionSummaryCard(summary: TopicDataSelectionSummary) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = BlueSoft.copy(alpha = 0.42f),
        border = BorderStroke(1.dp, SamsungBlue.copy(alpha = 0.16f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = summary.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = summary.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AppSecondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TopicSelectableItemsSection(
    items: List<TopicSelectableDataItem>,
    onToggleItem: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "자료",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        if (items.isEmpty()) {
            EmptyTopicDataSelectionRow()
        } else {
            items.forEach { item ->
                TopicSelectableItemRow(
                    item = item,
                    onClick = { onToggleItem(item.id) }
                )
            }
        }
    }
}

@Composable
private fun EmptyTopicDataSelectionRow() {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, AppOutline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "선택할 자료가 없습니다.",
            modifier = Modifier.padding(14.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = AppSecondaryText
        )
    }
}

@Composable
private fun TopicSelectableItemRow(
    item: TopicSelectableDataItem,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = if (item.isSelected) SamsungBlue.copy(alpha = 0.28f) else AppOutline
        ),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 74.dp)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TypeBadge(label = item.typeLabel)
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppSecondaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.meta,
                    style = MaterialTheme.typography.labelSmall,
                    color = AppSecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Checkbox(
                checked = item.isSelected,
                onCheckedChange = { onClick() }
            )
        }
    }
}

@Composable
private fun TypeBadge(label: String) {
    Text(
        text = label.take(1),
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(BlueSoft)
            .border(1.dp, SamsungBlue.copy(alpha = 0.16f), RoundedCornerShape(8.dp))
            .padding(top = 5.dp),
        style = MaterialTheme.typography.labelMedium,
        color = SamsungBlue,
        fontWeight = FontWeight.Bold
    )
}

@Preview(showBackground = true)
@Composable
private fun TopicDataSelectionScreenPreview() {
    SmartClipboardTheme {
        TopicDataSelectionScreen(
            state = TopicDataSelectionUiState(
                topicId = 1L,
                summary = TopicDataSelectionSummary(
                    title = "사용된 자료 2개",
                    subtitle = "이미지 1 · 링크 1"
                ),
                items = listOf(
                    TopicSelectableDataItem(
                        id = 1L,
                        title = "screenshot.png",
                        description = "분석 완료",
                        meta = "이미지 · 갤러리",
                        typeLabel = "이미지",
                        isSelected = true
                    ),
                    TopicSelectableDataItem(
                        id = 2L,
                        title = "예약 페이지",
                        description = "항공권 예약",
                        meta = "링크 · 공유",
                        typeLabel = "링크",
                        isSelected = false
                    )
                )
            )
        )
    }
}
