package com.smartclipboard.ai.presentation.analysis

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartclipboard.ai.export.notes.SamsungNotesExportLauncher
import com.smartclipboard.ai.export.notes.SamsungNotesExportResult
import com.smartclipboard.ai.presentation.analysis.action.TopicActionDraftSection
import com.smartclipboard.ai.presentation.analysis.action.TopicActionCardUiState
import com.smartclipboard.ai.presentation.analysis.action.TopicActionDraftUiState
import com.smartclipboard.ai.presentation.analysis.action.TopicActionDraftViewModel
import com.smartclipboard.ai.ui.theme.AppOutline
import com.smartclipboard.ai.ui.theme.AppSecondaryText
import com.smartclipboard.ai.ui.theme.BlueSoft
import com.smartclipboard.ai.ui.theme.SamsungBlue
import com.smartclipboard.ai.ui.theme.SmartClipboardTheme

@Composable
fun TopicAnalysisRoute(
    topicId: Long,
    modifier: Modifier = Modifier,
    viewModel: TopicAnalysisViewModel = hiltViewModel(),
    actionViewModel: TopicActionDraftViewModel = hiltViewModel(),
    onClose: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionUiState by actionViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(topicId) {
        viewModel.start(topicId)
        actionViewModel.start(topicId)
    }
    LaunchedEffect(topicId, uiState.isDone) {
        if (uiState.isDone) {
            actionViewModel.ensureDrafts(topicId)
        }
    }

    TopicAnalysisScreen(
        state = uiState,
        actionState = actionUiState,
        modifier = modifier,
        onRetry = viewModel::retry,
        onEditAction = actionViewModel::updateActionContent,
        onExportAction = { card ->
            when (
                SamsungNotesExportLauncher.export(
                    context = context,
                    title = card.title,
                    body = card.body
                )
            ) {
                SamsungNotesExportResult.Started -> {
                    Toast.makeText(context, "Samsung Notes로 보냅니다", Toast.LENGTH_SHORT).show()
                    actionViewModel.markActionExported(card.id)
                }

                SamsungNotesExportResult.AppNotFound -> {
                    Toast.makeText(context, "Samsung Notes를 찾을 수 없어요", Toast.LENGTH_SHORT).show()
                }

                SamsungNotesExportResult.EmptyContent -> {
                    Toast.makeText(context, "보낼 내용이 없어요", Toast.LENGTH_SHORT).show()
                }

                SamsungNotesExportResult.Failed -> {
                    Toast.makeText(context, "Samsung Notes로 보내지 못했어요", Toast.LENGTH_SHORT).show()
                }
            }
        },
        onCompleteAction = actionViewModel::completeAction,
        onCompleteAll = actionViewModel::completeAll,
        onClose = onClose
    )
}

@Composable
fun TopicAnalysisScreen(
    state: TopicAnalysisUiState,
    actionState: TopicActionDraftUiState = TopicActionDraftUiState(),
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
    onEditAction: (Long, String, String) -> Unit = { _, _, _ -> },
    onExportAction: (TopicActionCardUiState) -> Unit = {},
    onCompleteAction: (Long) -> Unit = {},
    onCompleteAll: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopicAnalysisHeader(onClose = onClose)
        TopicAnalysisStatusCard(state = state)
        TopicAnalysisSummaryCard(state = state)
        TopicAnalysisEvidenceSection(evidence = state.evidence)
        TopicActionDraftSection(
            state = actionState,
            onEditAction = onEditAction,
            onExportAction = onExportAction,
            onCompleteAction = onCompleteAction,
            onCompleteAll = onCompleteAll,
            onCloseKeepingIncomplete = onClose
        )
        if (state.canRetry) {
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("다시 분석")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun TopicAnalysisHeader(onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "AI 초안",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "선택한 자료를 바탕으로 정리합니다.",
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
private fun TopicAnalysisStatusCard(state: TopicAnalysisUiState) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = BlueSoft.copy(alpha = 0.42f),
        border = BorderStroke(1.dp, SamsungBlue.copy(alpha = 0.16f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = state.statusLabel,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = SamsungBlue,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TopicAnalysisSummaryCard(state: TopicAnalysisUiState) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, AppOutline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "요약",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = state.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun TopicAnalysisEvidenceSection(evidence: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "사용된 자료",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        if (evidence.isEmpty()) {
            EvidenceRow("분석 근거가 준비되면 여기에 표시됩니다.")
        } else {
            evidence.take(5).forEach { item ->
                EvidenceRow(item)
            }
        }
    }
}

@Composable
private fun EvidenceRow(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .border(1.dp, AppOutline, RoundedCornerShape(8.dp))
            .padding(12.dp),
        style = MaterialTheme.typography.bodySmall,
        color = AppSecondaryText,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Preview(showBackground = true)
@Composable
private fun TopicAnalysisScreenPreview() {
    SmartClipboardTheme {
        TopicAnalysisScreen(
            state = TopicAnalysisUiState(
                topicId = 1L,
                statusLabel = "분석 완료",
                summary = "출장 준비 자료를 정리했습니다.",
                evidence = listOf("dataItemId=1: 항공권 예약 정보", "dataItemId=2: 숙소 주소")
            )
        )
    }
}
