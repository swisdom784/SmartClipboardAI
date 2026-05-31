package com.smartclipboard.ai.processing.gemini.recommendation

import com.smartclipboard.ai.BuildConfig
import javax.inject.Inject

interface GeminiApiKeyProvider {
    val apiKey: String
}

class BuildConfigGeminiApiKeyProvider @Inject constructor() : GeminiApiKeyProvider {
    override val apiKey: String
        get() = BuildConfig.GEMINI_API_KEY
}

class StaticGeminiApiKeyProvider(
    override val apiKey: String
) : GeminiApiKeyProvider

class GeminiApiKeyMissingException : IllegalStateException("Gemini API key is missing")
