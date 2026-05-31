package com.smartclipboard.ai.presentation.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onOpenMaterials: () -> Unit = {},
    onTopicSelectionRequested: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.start()
    }
    LaunchedEffect(viewModel) {
        viewModel.topicCreatedEvents.collect { topicId ->
            onTopicSelectionRequested(topicId)
        }
    }

    HomeScreen(
        state = uiState,
        modifier = modifier,
        onSubmitRequest = viewModel::submitUserRequest,
        onOpenTask = { task ->
            when (task.kind) {
                HomeTaskKind.TOPIC -> task.topicIdOrNull()?.let(onTopicSelectionRequested)
                HomeTaskKind.RECOMMENDATION -> viewModel.openTask(task)
            }
        },
        onOpenMaterials = onOpenMaterials
    )
}

private fun HomeTaskItem.topicIdOrNull(): Long? {
    return id.removePrefix("topic:").toLongOrNull()
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    modifier: Modifier = Modifier,
    onSubmitRequest: (String) -> Unit = {},
    onOpenTask: (HomeTaskItem) -> Unit = {},
    onOpenMaterials: () -> Unit = {}
) {
    var draft by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        HomeHeader()
        NewTaskInput(
            value = draft,
            placeholder = state.inputPlaceholder,
            onValueChange = { draft = it },
            onSubmit = {
                val request = draft.trim()
                if (request.isNotEmpty()) {
                    onSubmitRequest(request)
                    draft = ""
                }
            }
        )
        AutoStatusPanel(
            summary = state.collectionSummary,
            hasReviewableRecommendations = state.hasReviewableRecommendations
        )
        HomeTaskList(
            tasks = state.tasks,
            onOpenTask = onOpenTask
        )
        RecentMaterialsPanel(
            summary = state.collectionSummary,
            materials = state.recentMaterials,
            onOpenMaterials = onOpenMaterials
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun HomeHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "SmartClipboard",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "저장된 자료를 정리하고 다음 작업으로 이어갑니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = AppSecondaryText
        )
    }
}

@Composable
private fun NewTaskInput(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, AppOutline)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 1,
                maxLines = 4,
                keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                shape = RoundedCornerShape(8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onSubmit,
                    enabled = value.isNotBlank(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("시작")
                }
            }
        }
    }
}

@Composable
private fun AutoStatusPanel(
    summary: HomeCollectionSummary,
    hasReviewableRecommendations: Boolean
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = BlueSoft.copy(alpha = 0.42f),
        border = BorderStroke(1.dp, SamsungBlue.copy(alpha = 0.16f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (hasReviewableRecommendations) SamsungBlue else AppSecondaryText.copy(alpha = 0.35f))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (hasReviewableRecommendations) "새 추천을 확인할 수 있습니다." else summary.title,
                    style = MaterialTheme.typography.bodyMedium,
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
}

@Composable
private fun HomeTaskList(
    tasks: List<HomeTaskItem>,
    onOpenTask: (HomeTaskItem) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "작업",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        if (tasks.isEmpty()) {
            EmptyTaskRow()
        } else {
            tasks.forEach { task ->
                HomeTaskRow(
                    task = task,
                    onClick = { onOpenTask(task) }
                )
            }
        }
    }
}

@Composable
private fun EmptyTaskRow() {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, AppOutline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "아직 진행 중인 작업이 없습니다.",
            modifier = Modifier.padding(14.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = AppSecondaryText
        )
    }
}

@Composable
private fun HomeTaskRow(
    task: HomeTaskItem,
    onClick: () -> Unit
) {
    val isCompleted = task.badges.contains(HomeTaskBadge.COMPLETED)
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = if (task.kind == HomeTaskKind.RECOMMENDATION) SamsungBlue.copy(alpha = 0.28f) else AppOutline
        ),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = if (isCompleted) 10.dp else 14.dp
            ),
            verticalArrangement = Arrangement.spacedBy(if (isCompleted) 6.dp else 9.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = task.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (task.kind == HomeTaskKind.RECOMMENDATION) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(SamsungBlue)
                    )
                }
            }
            if (!isCompleted && task.subtitle.isNotBlank()) {
                Text(
                    text = task.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppSecondaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            HomeBadgeRow(task.badges)
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun HomeBadgeRow(badges: List<HomeTaskBadge>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        badges.forEach { badge ->
            BadgePill(
                label = badge.label,
                emphasized = badge == HomeTaskBadge.REVIEW_REQUIRED
            )
        }
    }
}

@Composable
private fun BadgePill(
    label: String,
    emphasized: Boolean
) {
    Text(
        text = label,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (emphasized) SamsungBlue.copy(alpha = 0.10f) else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (emphasized) SamsungBlue.copy(alpha = 0.24f) else AppOutline,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelSmall,
        color = if (emphasized) SamsungBlue else AppSecondaryText,
        maxLines = 1
    )
}

@Composable
private fun RecentMaterialsPanel(
    summary: HomeCollectionSummary,
    materials: List<HomeMaterialItem>,
    onOpenMaterials: () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, AppOutline),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
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
                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "접기" else "보기")
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                materials.take(5).forEach { material ->
                    MaterialRow(material = material)
                }
                if (materials.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onOpenMaterials) {
                            Text("전체")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MaterialRow(material: HomeMaterialItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = material.type.label.take(1),
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(BlueSoft)
                .padding(top = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            color = SamsungBlue,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = material.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = material.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AppSecondaryText,
                maxLines = 1
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    SmartClipboardTheme {
        HomeScreen(
            state = HomeUiState(
                tasks = listOf(
                    HomeTaskItem(
                        id = "recommendation:1",
                        title = "최근 자료 정리",
                        subtitle = "새로 저장된 이미지와 링크를 한 번에 묶어볼 수 있습니다.",
                        kind = HomeTaskKind.RECOMMENDATION,
                        badges = listOf(HomeTaskBadge.AI_RECOMMENDATION, HomeTaskBadge.REVIEW_REQUIRED)
                    ),
                    HomeTaskItem(
                        id = "topic:1",
                        title = "출장 준비",
                        subtitle = "항공권, 숙소, 회의 메모",
                        kind = HomeTaskKind.TOPIC,
                        badges = listOf(HomeTaskBadge.USER_REQUEST, HomeTaskBadge.IN_PROGRESS)
                    ),
                    HomeTaskItem(
                        id = "topic:2",
                        title = "회의 내용 요약",
                        subtitle = "",
                        kind = HomeTaskKind.TOPIC,
                        badges = listOf(HomeTaskBadge.AI_RECOMMENDATION, HomeTaskBadge.COMPLETED)
                    )
                ),
                collectionSummary = HomeCollectionSummary(
                    title = "자료 8개 · 분석 중 2개",
                    subtitle = "이미지 4 · 링크 2 · 텍스트 2"
                ),
                recentMaterials = listOf(
                    HomeMaterialItem(
                        id = 1L,
                        title = "screenshot_0531.png",
                        subtitle = "분석 완료",
                        type = HomeMaterialType.IMAGE,
                        isAnalysisPending = false
                    ),
                    HomeMaterialItem(
                        id = 2L,
                        title = "제품 출시 일정",
                        subtitle = "분석 대기",
                        type = HomeMaterialType.LINK,
                        isAnalysisPending = true
                    )
                )
            )
        )
    }
}
