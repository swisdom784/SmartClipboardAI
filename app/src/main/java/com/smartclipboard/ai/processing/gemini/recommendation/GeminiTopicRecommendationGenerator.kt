package com.smartclipboard.ai.processing.gemini.recommendation

import com.smartclipboard.ai.domain.model.DataItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiTopicRecommendationGenerator @Inject constructor(
    private val apiKeyProvider: GeminiApiKeyProvider,
    private val client: GeminiTextClient,
    private val promptBuilder: GeminiRecommendationPromptBuilder
) : TopicRecommendationGenerator {
    internal constructor(
        apiKeyProvider: GeminiApiKeyProvider,
        client: GeminiTextClient,
        promptBuilder: GeminiRecommendationPromptBuilder,
        nowMillis: () -> Long
    ) : this(apiKeyProvider, client, promptBuilder) {
        this.nowMillis = nowMillis
    }

    private var nowMillis: () -> Long = { System.currentTimeMillis() }

    override suspend fun generate(items: List<DataItem>): List<TopicRecommendationCandidate> {
        val apiKey = apiKeyProvider.apiKey.trim()
        if (apiKey.isEmpty()) {
            throw GeminiApiKeyMissingException()
        }

        val prompt = promptBuilder.build(items)
        val modelText = client.generateText(apiKey = apiKey, prompt = prompt)
        return GeminiRecommendationParser.parse(
            modelText = modelText,
            createdAtMillis = nowMillis()
        )
    }
}
