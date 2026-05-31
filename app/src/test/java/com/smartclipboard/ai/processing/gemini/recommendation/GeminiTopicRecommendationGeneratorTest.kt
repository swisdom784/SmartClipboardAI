package com.smartclipboard.ai.processing.gemini.recommendation

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemEnrichment
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeminiTopicRecommendationGeneratorTest {
    @Test
    fun `passes BuildConfig style api key and prompt to Gemini client`() = runBlocking {
        val client = FakeGeminiTextClient(
            response = """
                {
                  "recommendations": [
                    {
                      "title": "최근 자료 정리",
                      "reason": "캡처와 링크가 함께 있어요.",
                      "prompt": "최근 저장한 자료를 정리해줘",
                      "dataItemIds": [7],
                      "confidence": 0.7
                    }
                  ]
                }
            """.trimIndent()
        )
        val generator = GeminiTopicRecommendationGenerator(
            apiKeyProvider = StaticGeminiApiKeyProvider("test-key"),
            client = client,
            promptBuilder = GeminiRecommendationPromptBuilder(),
            nowMillis = { 1_717_600_000_000L }
        )

        val recommendations = generator.generate(
            items = listOf(
                dataItem(
                    id = 7L,
                    type = DataItemType.LINK,
                    textContent = "https://example.com",
                    enrichment = DataItemEnrichment(
                        ogTitle = "예약 안내",
                        ogDescription = "방문 일정과 준비물"
                    )
                )
            )
        )

        assertEquals("test-key", client.apiKeys.single())
        assertTrue(client.prompts.single().contains("예약 안내"))
        assertEquals("최근 자료 정리", recommendations.single().title)
    }

    @Test(expected = GeminiApiKeyMissingException::class)
    fun `blank api key stops generation before client call`() {
        val client = FakeGeminiTextClient(response = "{}")
        val generator = GeminiTopicRecommendationGenerator(
            apiKeyProvider = StaticGeminiApiKeyProvider(" "),
            client = client,
            promptBuilder = GeminiRecommendationPromptBuilder()
        )

        runBlocking {
            generator.generate(items = listOf(dataItem(id = 1L)))
        }
    }

    private fun dataItem(
        id: Long,
        type: DataItemType = DataItemType.TEXT,
        textContent: String? = "회의 메모",
        enrichment: DataItemEnrichment = DataItemEnrichment()
    ): DataItem {
        return DataItem(
            id = id,
            type = type,
            source = DataItemSource.MANUAL,
            textContent = textContent,
            sourceUri = null,
            capturedAtMillis = 1_000L,
            createdAtMillis = 1_000L,
            updatedAtMillis = 1_000L,
            enrichment = enrichment
        )
    }

    private class FakeGeminiTextClient(
        private val response: String
    ) : GeminiTextClient {
        val apiKeys = mutableListOf<String>()
        val prompts = mutableListOf<String>()

        override suspend fun generateText(apiKey: String, prompt: String): String {
            apiKeys += apiKey
            prompts += prompt
            return response
        }
    }
}
