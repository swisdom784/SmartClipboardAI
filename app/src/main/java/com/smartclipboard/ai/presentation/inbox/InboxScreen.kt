package com.smartclipboard.ai.presentation.inbox

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
fun InboxRoute(
    modifier: Modifier = Modifier,
    viewModel: InboxViewModel = hiltViewModel(),
    onPickFilesRequested: () -> Unit = {},
    onAddToTopicRequested: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InboxScreen(
        state = uiState,
        modifier = modifier,
        onPickFilesRequested = onPickFilesRequested,
        onCategorySelected = viewModel::selectCategory,
        onViewModeToggle = viewModel::toggleViewMode,
        onToggleImportant = viewModel::toggleImportant,
        onDeleteItem = viewModel::deleteItem,
        onAddToTopicRequested = onAddToTopicRequested
    )
}

@Composable
fun InboxScreen(
    state: InboxUiState,
    modifier: Modifier = Modifier,
    onPickFilesRequested: () -> Unit = {},
    onCategorySelected: (InboxCategoryId) -> Unit = {},
    onViewModeToggle: () -> Unit = {},
    onToggleImportant: (Long) -> Unit = {},
    onDeleteItem: (Long) -> Unit = {},
    onAddToTopicRequested: (Long) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        InboxHeader(onPickFilesRequested = onPickFilesRequested)
        InboxCategoryRail(
            categories = state.categories,
            onCategorySelected = onCategorySelected
        )
        InboxItemsSection(
            state = state,
            onViewModeToggle = onViewModeToggle,
            onToggleImportant = onToggleImportant,
            onDeleteItem = onDeleteItem,
            onAddToTopicRequested = onAddToTopicRequested
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun InboxHeader(onPickFilesRequested: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "자료",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "이미지, 링크, 텍스트, 파일",
                style = MaterialTheme.typography.bodyMedium,
                color = AppSecondaryText
            )
        }
        Button(
            onClick = onPickFilesRequested,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("추가")
        }
    }
}

@Composable
private fun InboxCategoryRail(
    categories: List<InboxCategoryItem>,
    onCategorySelected: (InboxCategoryId) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        categories.forEach { category ->
            CategoryCard(
                category = category,
                onClick = { onCategorySelected(category.id) }
            )
        }
    }
}

@Composable
private fun CategoryCard(
    category: InboxCategoryItem,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (category.isSelected) BlueSoft.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = if (category.isSelected) SamsungBlue.copy(alpha = 0.28f) else AppOutline
        ),
        modifier = Modifier
            .width(128.dp)
            .heightIn(min = 88.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = category.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Text(
                text = "${category.count}개",
                style = MaterialTheme.typography.titleMedium,
                color = if (category.isSelected) SamsungBlue else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = category.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = AppSecondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun InboxItemsSection(
    state: InboxUiState,
    onViewModeToggle: () -> Unit,
    onToggleImportant: (Long) -> Unit,
    onDeleteItem: (Long) -> Unit,
    onAddToTopicRequested: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.selectedCategoryTitle,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = onViewModeToggle) {
                Text(
                    text = when (state.viewMode) {
                        InboxViewMode.LIST -> "그리드"
                        InboxViewMode.GRID -> "리스트"
                    },
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        if (state.visibleItems.isEmpty()) {
            EmptyInboxRow()
        } else {
            when (state.viewMode) {
                InboxViewMode.LIST -> InboxList(
                    items = state.visibleItems,
                    onToggleImportant = onToggleImportant,
                    onDeleteItem = onDeleteItem,
                    onAddToTopicRequested = onAddToTopicRequested
                )
                InboxViewMode.GRID -> InboxGrid(
                    items = state.visibleItems,
                    onToggleImportant = onToggleImportant,
                    onDeleteItem = onDeleteItem,
                    onAddToTopicRequested = onAddToTopicRequested
                )
            }
        }
    }
}

@Composable
private fun EmptyInboxRow() {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, AppOutline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "표시할 자료가 없습니다.",
            modifier = Modifier.padding(14.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = AppSecondaryText
        )
    }
}

@Composable
private fun InboxList(
    items: List<InboxDataItem>,
    onToggleImportant: (Long) -> Unit,
    onDeleteItem: (Long) -> Unit,
    onAddToTopicRequested: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items.forEach { item ->
            InboxItemCard(
                item = item,
                compact = false,
                onToggleImportant = { onToggleImportant(item.id) },
                onDeleteItem = { onDeleteItem(item.id) },
                onAddToTopicRequested = { onAddToTopicRequested(item.id) }
            )
        }
    }
}

@Composable
private fun InboxGrid(
    items: List<InboxDataItem>,
    onToggleImportant: (Long) -> Unit,
    onDeleteItem: (Long) -> Unit,
    onAddToTopicRequested: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { item ->
                    Box(modifier = Modifier.weight(1f)) {
                        InboxItemCard(
                            item = item,
                            compact = true,
                            onToggleImportant = { onToggleImportant(item.id) },
                            onDeleteItem = { onDeleteItem(item.id) },
                            onAddToTopicRequested = { onAddToTopicRequested(item.id) }
                        )
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun InboxItemCard(
    item: InboxDataItem,
    compact: Boolean,
    onToggleImportant: () -> Unit,
    onDeleteItem: () -> Unit,
    onAddToTopicRequested: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = if (item.isImportant) SamsungBlue.copy(alpha = 0.28f) else AppOutline
        ),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TypeBadge(label = item.typeLabel)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = if (compact) 2 else 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = AppSecondaryText,
                maxLines = if (compact) 2 else 3,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.meta,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    color = AppSecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.isAnalysisPending) {
                    Text(
                        text = "분석 중",
                        style = MaterialTheme.typography.labelSmall,
                        color = SamsungBlue
                    )
                }
            }
            InboxItemActions(
                isImportant = item.isImportant,
                onToggleImportant = onToggleImportant,
                onDeleteItem = onDeleteItem,
                onAddToTopicRequested = onAddToTopicRequested
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

@Composable
private fun InboxItemActions(
    isImportant: Boolean,
    onToggleImportant: () -> Unit,
    onDeleteItem: () -> Unit,
    onAddToTopicRequested: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onToggleImportant) {
            Text(if (isImportant) "해제" else "중요")
        }
        TextButton(onClick = onAddToTopicRequested) {
            Text("Topic")
        }
        TextButton(onClick = onDeleteItem) {
            Text("삭제")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InboxScreenPreview() {
    SmartClipboardTheme {
        InboxScreen(
            state = InboxUiState(
                categories = InboxCategoryId.entries.mapIndexed { index, id ->
                    InboxCategoryItem(
                        id = id,
                        title = id.label,
                        subtitle = "자료",
                        count = 6 - index,
                        isSelected = id == InboxCategoryId.RECENT
                    )
                },
                visibleItems = listOf(
                    InboxDataItem(
                        id = 1L,
                        title = "screenshot_0531.png",
                        description = "분석 완료",
                        meta = "이미지 · 갤러리",
                        typeLabel = "이미지",
                        isImportant = true,
                        isAnalysisPending = false
                    ),
                    InboxDataItem(
                        id = 2L,
                        title = "https://example.com",
                        description = "제품 출시 일정",
                        meta = "링크 · 공유",
                        typeLabel = "링크",
                        isImportant = false,
                        isAnalysisPending = true
                    )
                )
            )
        )
    }
}
