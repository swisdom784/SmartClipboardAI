package com.smartclipboard.ai.presentation.settings

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
fun SettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    onRequestImagePermission: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        state = uiState,
        modifier = modifier,
        onCollectionWindowSelected = viewModel::selectCollectionWindow,
        onCustomHoursApplied = viewModel::applyCustomCollectionHours,
        onQuotaSelected = viewModel::setQuotaBytes,
        onCleanupStorage = viewModel::cleanupStorage,
        onRequestImagePermission = {
            onRequestImagePermission()
            viewModel.refreshPermissionState()
        }
    )
}

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    modifier: Modifier = Modifier,
    onCollectionWindowSelected: (CollectionWindowOption) -> Unit = {},
    onCustomHoursApplied: (Int) -> Unit = {},
    onQuotaSelected: (Long) -> Unit = {},
    onCleanupStorage: () -> Unit = {},
    onRequestImagePermission: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        SettingsHeader()
        CollectionWindowSection(
            state = state,
            onCollectionWindowSelected = onCollectionWindowSelected,
            onCustomHoursApplied = onCustomHoursApplied
        )
        StorageSection(
            storage = state.storage,
            onQuotaSelected = onQuotaSelected,
            onCleanupStorage = onCleanupStorage
        )
        PermissionSection(
            permission = state.permission,
            onRequestImagePermission = onRequestImagePermission
        )
        GeminiSection(gemini = state.gemini)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun SettingsHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "설정",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "수집 범위와 저장 공간",
            style = MaterialTheme.typography.bodyMedium,
            color = AppSecondaryText
        )
    }
}

@Composable
private fun CollectionWindowSection(
    state: SettingsUiState,
    onCollectionWindowSelected: (CollectionWindowOption) -> Unit,
    onCustomHoursApplied: (Int) -> Unit
) {
    var customHoursText by rememberSaveable { mutableStateOf(state.customHours.toString()) }

    SettingsSurface {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionTitle(
                title = "수집 기간",
                subtitle = state.selectedCollectionWindowLabel
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.collectionWindowOptions.forEach { option ->
                    SelectablePill(
                        label = option.label,
                        selected = option.isSelected,
                        onClick = { onCollectionWindowSelected(option.option) }
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = customHoursText,
                    onValueChange = { value ->
                        customHoursText = value.filter { it.isDigit() }.take(4)
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("시간") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(8.dp)
                )
                Button(
                    onClick = {
                        onCustomHoursApplied(customHoursText.toIntOrNull() ?: state.customHours)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("적용")
                }
            }
        }
    }
}

@Composable
private fun StorageSection(
    storage: SettingsStorageUi,
    onQuotaSelected: (Long) -> Unit,
    onCleanupStorage: () -> Unit
) {
    SettingsSurface {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionTitle(
                title = "저장 공간",
                subtitle = storage.subtitle
            )
            Text(
                text = storage.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            LinearProgressIndicator(
                progress = { storage.usedPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = if (storage.isOverQuota) MaterialTheme.colorScheme.error else SamsungBlue,
                trackColor = BlueSoft
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = storage.caption,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (storage.isOverQuota) MaterialTheme.colorScheme.error else AppSecondaryText
                )
                TextButton(onClick = onCleanupStorage) {
                    Text("정리")
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                storage.quotaOptions.forEach { option ->
                    SelectablePill(
                        label = option.label,
                        selected = option.isSelected,
                        onClick = { onQuotaSelected(option.bytes) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionSection(
    permission: SettingsPermissionUi,
    onRequestImagePermission: () -> Unit
) {
    SettingsSurface {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = permission.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = permission.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppSecondaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (permission.showAction) {
                Button(
                    onClick = onRequestImagePermission,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(permission.actionLabel)
                }
            }
        }
    }
}

@Composable
private fun GeminiSection(gemini: SettingsGeminiUi) {
    SettingsSurface {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionTitle(
                title = "Gemini",
                subtitle = gemini.title
            )
            Text(
                text = gemini.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (gemini.needsAttention) {
                    MaterialTheme.colorScheme.error
                } else {
                    AppSecondaryText
                },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SettingsSurface(content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, AppOutline),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = AppSecondaryText,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SelectablePill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = label,
        modifier = Modifier
            .heightIn(min = 36.dp)
            .background(
                color = if (selected) BlueSoft else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = if (selected) SamsungBlue.copy(alpha = 0.28f) else AppOutline,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        style = MaterialTheme.typography.labelMedium,
        color = if (selected) SamsungBlue else AppSecondaryText
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SmartClipboardTheme {
        SettingsScreen(
            state = SettingsUiStateMapper.map(
                settings = SmartClipboardSettings(
                    collectionWindow = CollectionWindowOption.LAST_24_HOURS,
                    customHours = 12,
                    quotaBytes = DEFAULT_QUOTA_BYTES
                ),
                storageUsage = com.smartclipboard.ai.storage.StorageUsageSummary(
                    usedBytes = 180L * 1024L * 1024L,
                    quotaBytes = DEFAULT_QUOTA_BYTES,
                    overQuotaBytes = 0L,
                    itemCount = 42
                ),
                permissionState = SettingsPermissionState(
                    isGranted = true,
                    requiredPermissions = emptyList()
                )
            )
        )
    }
}
