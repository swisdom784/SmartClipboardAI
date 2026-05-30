package com.smartclipboard.ai.presentation.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationContractTest {
    @Test
    fun topLevelDestinationsStayInExpectedOrder() {
        assertEquals(
            listOf("Home", "Inbox", "Logs", "Settings"),
            TopLevelDestination.entries.map { it.label }
        )
    }

    @Test
    fun homeIsDefaultTopLevelDestination() {
        assertEquals(TopLevelDestination.Home, TopLevelDestination.default)
        assertEquals(AppRoute.Home.route, TopLevelDestination.default.route)
    }

    @Test
    fun detailRoutesUseStableTopicIds() {
        assertEquals("topic/create", AppRoute.TopicCreate.route)
        assertEquals("topic/42/select", AppRoute.TopicDataSelection.createRoute(topicId = 42L))
        assertEquals("topic/42/analysis", AppRoute.TopicAnalysis.createRoute(topicId = 42L))
    }
}
