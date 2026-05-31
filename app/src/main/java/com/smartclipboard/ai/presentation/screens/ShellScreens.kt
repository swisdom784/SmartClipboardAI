package com.smartclipboard.ai.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smartclipboard.ai.presentation.home.HomeRoute
import com.smartclipboard.ai.presentation.inbox.InboxRoute
import com.smartclipboard.ai.presentation.logs.LogsRoute
import com.smartclipboard.ai.presentation.settings.SettingsRoute

@Composable
fun HomeShellScreen(modifier: Modifier = Modifier) {
    HomeRoute(modifier = modifier)
}

@Composable
fun InboxShellScreen(
    modifier: Modifier = Modifier,
    onPickFilesRequested: () -> Unit = {}
) {
    InboxRoute(
        modifier = modifier,
        onPickFilesRequested = onPickFilesRequested
    )
}

@Composable
fun LogsShellScreen(modifier: Modifier = Modifier) {
    LogsRoute(modifier = modifier)
}

@Composable
fun SettingsShellScreen(
    modifier: Modifier = Modifier,
    onRequestImagePermission: () -> Unit = {}
) {
    SettingsRoute(
        modifier = modifier,
        onRequestImagePermission = onRequestImagePermission
    )
}

@Composable
fun TopicCreateShellScreen(modifier: Modifier = Modifier) {
    ShellScreen(
        title = "Topic",
        subtitle = "새 작업",
        modifier = modifier
    )
}

@Composable
fun TopicDataSelectionShellScreen(modifier: Modifier = Modifier) {
    ShellScreen(
        title = "자료 선택",
        subtitle = "Topic에 사용할 자료",
        modifier = modifier
    )
}

@Composable
fun TopicAnalysisShellScreen(modifier: Modifier = Modifier) {
    ShellScreen(
        title = "AI 초안",
        subtitle = "검토 후 실행",
        modifier = modifier
    )
}

@Composable
private fun ShellScreen(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        action?.invoke()
    }
}
