package com.smartclipboard.ai.presentation.navigation

sealed class AppRoute(open val route: String) {
    data object Home : AppRoute("home")
    data object Inbox : AppRoute("inbox")
    data object Logs : AppRoute("logs")
    data object Settings : AppRoute("settings")
    data object TopicCreate : AppRoute("topic/create")

    data object TopicDataSelection : AppRoute("topic/{topicId}/select") {
        fun createRoute(topicId: Long): String = "topic/$topicId/select"
    }

    data object TopicAnalysis : AppRoute("topic/{topicId}/analysis") {
        fun createRoute(topicId: Long): String = "topic/$topicId/analysis"
    }
}

enum class TopLevelDestination(
    val route: String,
    val label: String
) {
    Home(AppRoute.Home.route, "Home"),
    Inbox(AppRoute.Inbox.route, "Inbox"),
    Logs(AppRoute.Logs.route, "Logs"),
    Settings(AppRoute.Settings.route, "Settings");

    companion object {
        val default: TopLevelDestination = Home

        fun fromRoute(route: String): TopLevelDestination {
            return entries.firstOrNull { it.route == route } ?: default
        }
    }
}
