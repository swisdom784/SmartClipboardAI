package com.smartclipboard.ai.processing.gemini.recommendation

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GeminiTopicRecommendationManagerTest {
    @Test
    fun `refresh stores only the latest recommendation session`() = runBlocking {
        val store = InMemoryRecommendationSessionStore()
        val manager = GeminiTopicRecommendationManager(
            dataSource = FakeRecommendationDataSource(listOf(dataItem(id = 1L))),
            generator = FakeTopicRecommendationGenerator(
                listOf(
                    TopicRecommendationCandidate(
                        id = "older",
                        title = "이전 추천",
                        reason = "이전 자료",
                        prompt = "이전 자료 정리",
                        sourceDataItemIds = listOf(1L),
                        createdAtMillis = 1000L
                    )
                ),
                listOf(
                    TopicRecommendationCandidate(
                        id = "new",
                        title = "새 추천",
                        reason = "새 자료",
                        prompt = "새 자료 정리",
                        sourceDataItemIds = listOf(1L),
                        createdAtMillis = 2000L
                    )
                )
            ),
            sessionStore = store,
            nowMillis = { 2_000L }
        )

        manager.refresh(limit = 10)
        val second = manager.refresh(limit = 10)

        assertEquals(RecommendationSessionStatus.READY, second.status)
        assertEquals(listOf("new"), store.currentSession.value?.recommendations?.map { it.id })
    }

    @Test
    fun `missing api key stores skipped session without throwing`() = runBlocking {
        val store = InMemoryRecommendationSessionStore()
        val manager = GeminiTopicRecommendationManager(
            dataSource = FakeRecommendationDataSource(listOf(dataItem(id = 1L))),
            generator = ThrowingTopicRecommendationGenerator(GeminiApiKeyMissingException()),
            sessionStore = store,
            nowMillis = { 3_000L }
        )

        val session = manager.refresh(limit = 10)

        assertEquals(RecommendationSessionStatus.SKIPPED, session.status)
        assertEquals(emptyList<TopicRecommendationCandidate>(), session.recommendations)
        assertEquals(session, store.currentSession.value)
    }

    @Test
    fun `network failure stores failed session without throwing`() = runBlocking {
        val store = InMemoryRecommendationSessionStore()
        val manager = GeminiTopicRecommendationManager(
            dataSource = FakeRecommendationDataSource(listOf(dataItem(id = 1L))),
            generator = ThrowingTopicRecommendationGenerator(IllegalStateException("Network failed")),
            sessionStore = store,
            nowMillis = { 4_000L }
        )

        val session = manager.refresh(limit = 10)

        assertEquals(RecommendationSessionStatus.FAILED, session.status)
        assertEquals("추천을 준비하지 못했어요", session.message)
        assertEquals(session, store.currentSession.value)
    }

    private fun dataItem(id: Long): DataItem {
        return DataItem(
            id = id,
            type = DataItemType.TEXT,
            source = DataItemSource.MANUAL,
            textContent = "회의 메모",
            capturedAtMillis = 1_000L,
            createdAtMillis = 1_000L,
            updatedAtMillis = 1_000L
        )
    }

    private class FakeRecommendationDataSource(
        private val items: List<DataItem>
    ) : RecommendationDataSource {
        override suspend fun getRecommendationInputItems(limit: Int): List<DataItem> {
            return items.take(limit)
        }
    }

    private class FakeTopicRecommendationGenerator(
        private vararg val batches: List<TopicRecommendationCandidate>
    ) : TopicRecommendationGenerator {
        private var index = 0

        override suspend fun generate(items: List<DataItem>): List<TopicRecommendationCandidate> {
            return batches[index++]
        }
    }

    private class ThrowingTopicRecommendationGenerator(
        private val throwable: Throwable
    ) : TopicRecommendationGenerator {
        override suspend fun generate(items: List<DataItem>): List<TopicRecommendationCandidate> {
            throw throwable
        }
    }
}
