package com.smartclipboard.ai.presentation.analysis.action

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.smartclipboard.ai.ui.theme.AppOutline
import com.smartclipboard.ai.ui.theme.AppSecondaryText
import com.smartclipboard.ai.ui.theme.SamsungBlue

@Composable
fun TopicActionDraftSection(
    state: TopicActionDraftUiState,
    modifier: Modifier = Modifier,
    onEditAction: (Long, String, String) -> Unit = { _, _, _ -> },
    onExportAction: (TopicActionCardUiState) -> Unit = {},
    onCompleteAction: (Long) -> Unit = {},
    onCompleteAll: () -> Unit = {},
    onCloseKeepingIncomplete: () -> Unit = {}
) {
    var editingCard by remember { mutableStateOf<TopicActionEditDraft?>(null) }
    var showBackDialog by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = state.cards.isNotEmpty() && !state.allRequiredCompleted) {
        showBackDialog = true
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "보낼 초안",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (state.cards.isNotEmpty() && !state.allRequiredCompleted) {
                TextButton(onClick = onCompleteAll) {
                    Text("즉시 완료")
                }
            }
        }

        if (state.cards.isEmpty()) {
            EmptyActionDraftRow()
        } else {
            state.cards.forEach { card ->
                TopicActionDraftCard(
                    card = card,
                    onEdit = {
                        editingCard = TopicActionEditDraft(
                            id = card.id,
                            title = card.title,
                            body = card.body
                        )
                    },
                    onExport = { onExportAction(card) },
                    onComplete = { onCompleteAction(card.id) }
                )
            }
        }

        Text(
            text = state.footerMessage,
            style = MaterialTheme.typography.bodySmall,
            color = AppSecondaryText
        )
    }

    editingCard?.let { draft ->
        TopicActionEditDialog(
            draft = draft,
            onDismiss = { editingCard = null },
            onSave = { title, body ->
                onEditAction(draft.id, title, body)
                editingCard = null
            }
        )
    }

    if (showBackDialog) {
        IncompleteActionDialog(
            onDismiss = { showBackDialog = false },
            onComplete = {
                onCompleteAll()
                onCloseKeepingIncomplete()
                showBackDialog = false
            },
            onKeepIncomplete = {
                showBackDialog = false
                onCloseKeepingIncomplete()
            }
        )
    }
}

@Composable
private fun EmptyActionDraftRow() {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, AppOutline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "분석이 완료되면 초안 카드가 표시됩니다.",
            modifier = Modifier.padding(14.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = AppSecondaryText
        )
    }
}

@Composable
private fun TopicActionDraftCard(
    card: TopicActionCardUiState,
    onEdit: () -> Unit,
    onExport: () -> Unit,
    onComplete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = if (card.isCompleted) SamsungBlue.copy(alpha = 0.22f) else AppOutline
        ),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .heightIn(min = if (card.isCollapsed) 58.dp else 110.dp)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = if (card.isCollapsed) 10.dp else 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(if (card.isCollapsed) 4.dp else 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = card.typeLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = SamsungBlue,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = card.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = card.statusLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = AppSecondaryText
                )
            }

            if (!card.isCollapsed) {
                Text(
                    text = card.previewText,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppSecondaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onEdit) {
                        Text("수정")
                    }
                    if (card.canExportToNotes) {
                        TextButton(onClick = onExport) {
                            Text("노트로 보내기")
                        }
                    }
                    if (card.canExportToCalendar) {
                        TextButton(onClick = onExport) {
                            Text("캘린더로 보내기")
                        }
                    }
                    if (card.canExportToReminder) {
                        TextButton(onClick = onExport) {
                            Text("리마인더로 보내기")
                        }
                    }
                    TextButton(onClick = onComplete) {
                        Text("완료")
                    }
                }
            }
        }
    }
}

@Composable
private fun TopicActionEditDialog(
    draft: TopicActionEditDraft,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by rememberSaveable(draft.id) { mutableStateOf(draft.title) }
    var body by rememberSaveable(draft.id) { mutableStateOf(draft.body) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("초안 수정") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("제목") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text("내용") },
                    minLines = 4,
                    maxLines = 8
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(title, body) }) {
                Text("저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
private fun IncompleteActionDialog(
    onDismiss: () -> Unit,
    onComplete: () -> Unit,
    onKeepIncomplete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("작업을 닫을까요?") },
        text = { Text("초안 검토가 아직 남아 있습니다.") },
        confirmButton = {
            Button(onClick = onComplete) {
                Text("완료")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onKeepIncomplete) {
                    Text("미완료")
                }
                TextButton(onClick = onDismiss) {
                    Text("계속 보기")
                }
            }
        }
    )
}

private data class TopicActionEditDraft(
    val id: Long,
    val title: String,
    val body: String
)
