package com.smartclipboard.ai.processing.gemini.analysis

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemEnrichment
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.Topic
import com.smartclipboard.ai.domain.model.TopicOrigin
import com.smartclipboard.ai.processing.gemini.recommendation.GeminiApiKeyMissingException
import com.smartclipboard.ai.processing.gemini.recommendation.GeminiTextClient
import com.smartclipboard.ai.processing.gemini.recommendation.StaticGeminiApiKeyProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeminiTopicAnalysisGeneratorTest {
    @Test
    fun passesApiKeyAndPromptToGeminiClient() = runBlocking {
        val client = FakeGeminiTextClient(
            response = """
                {
                  "summary": "회의 자료를 요약했습니다.",
                  "evidence": [
                    { "dataItemId": 5, "note": "회의 메모" }
                  ]
                }
            """.trimIndent()
        )
        val generator = GeminiTopicAnalysisGenerator(
            apiKeyProvider = StaticGeminiApiKeyProvider("test-key"),
            client = client,
            promptBuilder = GeminiTopicAnalysisPromptBuilder()
        )

        val draft = generator.generate(
            TopicAnalysisInput(
                topic = topic(),
                selectedItems = listOf(dataItem(id = 5L))
            )
        )

        assertEquals("test-key", client.apiKey)
        assertTrue(client.prompt.contains("회의 자료 정리"))
        assertTrue(client.prompt.contains("id=5"))
        assertEquals("회의 자료를 요약했습니다.", draft.summary)
        assertEquals(listOf("dataItemId=5: 회의 메모"), draft.evidence)
    }

    @Test(expected = GeminiApiKeyMissingException::class)
    fun throwsWhenApiKeyIsMissing() {
        val generator = GeminiTopicAnalysisGenerator(
            apiKeyProvider = StaticGeminiApiKeyProvider(" "),
            client = FakeGeminiTextClient(response = "{}"),
            promptBuilder = GeminiTopicAnalysisPromptBuilder()
        )

        runBlocking {
            generator.generate(
                TopicAnalysisInput(
                    topic = topic(),
                    selectedItems = listOf(dataItem(id = 5L))
                )
            )
        }
    }

    private class FakeGeminiTextClient(
        private val response: String
    ) : GeminiTextClient {
        var apiKey: String = ""
        var prompt: String = ""

        override suspend fun generateText(apiKey: String, prompt: String): String {
            this.apiKey = apiKey
            this.prompt = prompt
            return response
        }
    }

    private fun topic(): Topic {
        return Topic(
            id = 4L,
            title = "회의 자료 정리",
            prompt = "방금 저장한 회의 자료 정리해줘",
            origin = TopicOrigin.USER_REQUEST,
            createdAtMillis = 1L,
            updatedAtMillis = 1L
        )
    }

    private fun dataItem(id: Long): DataItem {
        return DataItem(
            id = id,
            type = DataItemType.LINK,
            source = DataItemSource.SHARE_TARGET,
            textContent = "https://example.com",
            capturedAtMillis = id,
            createdAtMillis = id,
            updatedAtMillis = id,
            enrichment = DataItemEnrichment(
                ogTitle = "회의 메모",
                ogDescription = "다음 회의 안건"
            )
        )
    }
}
