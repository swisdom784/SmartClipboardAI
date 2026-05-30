package com.smartclipboard.ai.presentation.navigation

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

@Composable
fun SmartClipboardRoot() {
    var selectedDestinationRoute by rememberSaveable {
        mutableStateOf(TopLevelDestination.default.route)
    }
    val selectedDestination = TopLevelDestination.fromRoute(selectedDestinationRoute)

    Scaffold(
        bottomBar = {
            SmartClipboardBottomBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = { selectedDestinationRoute = it.route }
            )
        }
    ) { contentPadding ->
        TopLevelDestinationContent(
            selectedDestination = selectedDestination,
            contentPadding = contentPadding
        )
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
    contentPadding: PaddingValues
) {
    when (selectedDestination) {
        TopLevelDestination.Home -> HomeShellScreen(Modifier.padding(contentPadding))
        TopLevelDestination.Inbox -> InboxShellScreen(Modifier.padding(contentPadding))
        TopLevelDestination.Logs -> LogsShellScreen(Modifier.padding(contentPadding))
        TopLevelDestination.Settings -> SettingsShellScreen(Modifier.padding(contentPadding))
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
