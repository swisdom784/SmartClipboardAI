package com.smartclipboard.ai.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smartclipboard.ai.presentation.screens.HomeShellScreen
import com.smartclipboard.ai.presentation.screens.InboxShellScreen
import com.smartclipboard.ai.presentation.screens.LogsShellScreen
import com.smartclipboard.ai.presentation.screens.SettingsShellScreen
import com.smartclipboard.ai.presentation.screens.TopicDataSelectionShellScreen

@Composable
fun SmartClipboardRoot(
    onPickFilesRequested: () -> Unit = {},
    onRequestImagePermission: () -> Unit = {}
) {
    var selectedDestinationRoute by rememberSaveable {
        mutableStateOf(TopLevelDestination.default.route)
    }
    var selectedTopicId by rememberSaveable {
        mutableStateOf<Long?>(null)
    }
    val selectedDestination = TopLevelDestination.fromRoute(selectedDestinationRoute)
    BackHandler(enabled = selectedTopicId != null) {
        selectedTopicId = null
    }

    Scaffold(
        bottomBar = {
            if (selectedTopicId == null) {
                SmartClipboardBottomBar(
                    selectedDestination = selectedDestination,
                    onDestinationSelected = { selectedDestinationRoute = it.route }
                )
            }
        }
    ) { contentPadding ->
        val topicId = selectedTopicId
        if (topicId != null) {
            TopicDataSelectionShellScreen(
                topicId = topicId,
                modifier = Modifier.padding(contentPadding),
                onClose = { selectedTopicId = null }
            )
        } else {
            TopLevelDestinationContent(
                selectedDestination = selectedDestination,
                contentPadding = contentPadding,
                onPickFilesRequested = onPickFilesRequested,
                onRequestImagePermission = onRequestImagePermission,
                onTopicSelectionRequested = { selectedTopicId = it }
            )
        }
    }
}

@Composable
private fun SmartClipboardBottomBar(
    selectedDestination: TopLevelDestination,
    onDestinationSelected: (TopLevelDestination) -> Unit
) {
    NavigationBar {
        TopLevelDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = selectedDestination == destination,
                onClick = { onDestinationSelected(destination) },
                label = { Text(destination.label) },
                icon = {}
            )
        }
    }
}

@Composable
private fun TopLevelDestinationContent(
    selectedDestination: TopLevelDestination,
    contentPadding: PaddingValues,
    onPickFilesRequested: () -> Unit,
    onRequestImagePermission: () -> Unit,
    onTopicSelectionRequested: (Long) -> Unit
) {
    when (selectedDestination) {
        TopLevelDestination.Home -> HomeShellScreen(
            modifier = Modifier.padding(contentPadding),
            onTopicSelectionRequested = onTopicSelectionRequested
        )
        TopLevelDestination.Inbox -> InboxShellScreen(
            modifier = Modifier.padding(contentPadding),
            onPickFilesRequested = onPickFilesRequested
        )
        TopLevelDestination.Logs -> LogsShellScreen(Modifier.padding(contentPadding))
        TopLevelDestination.Settings -> SettingsShellScreen(
            modifier = Modifier.padding(contentPadding),
            onRequestImagePermission = onRequestImagePermission
        )
    }
}

@Composable
fun DetailShellScreen(
    title: String,
    modifier: Modifier = Modifier
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
            style = MaterialTheme.typography.headlineSmall
        )
    }
}
