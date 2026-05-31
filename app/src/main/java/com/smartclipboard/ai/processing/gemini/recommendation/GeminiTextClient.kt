package com.smartclipboard.ai.processing.gemini.recommendation

interface GeminiTextClient {
    suspend fun generateText(apiKey: String, prompt: String): String
}
