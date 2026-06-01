package com.smartclipboard.ai.processing.gemini.analysis

import com.smartclipboard.ai.processing.gemini.recommendation.GeminiApiKeyMissingException
import com.smartclipboard.ai.processing.gemini.recommendation.GeminiApiKeyProvider
import com.smartclipboard.ai.processing.gemini.recommendation.GeminiTextClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiTopicAnalysisGenerator @Inject constructor(
    private val apiKeyProvider: GeminiApiKeyProvider,
    private val client: GeminiTextClient,
    private val promptBuilder: GeminiTopicAnalysisPromptBuilder
) : TopicAnalysisGenerator {
    override suspend fun generate(input: TopicAnalysisInput): TopicAnalysisDraft {
        val apiKey = apiKeyProvider.apiKey.trim()
        if (apiKey.isEmpty()) {
            throw GeminiApiKeyMissingException()
        }

        val modelText = client.generateText(
            apiKey = apiKey,
            prompt = promptBuilder.build(input)
        )
        return GeminiTopicAnalysisParser.parse(
            modelText = modelText,
            modelName = MODEL_NAME
        )
    }

    private companion object {
        const val MODEL_NAME = "gemini-2.5-flash"
    }
}
