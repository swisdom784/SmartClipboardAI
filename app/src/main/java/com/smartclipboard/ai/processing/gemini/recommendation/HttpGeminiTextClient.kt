package com.smartclipboard.ai.processing.gemini.recommendation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HttpGeminiTextClient @Inject constructor() : GeminiTextClient {
    override suspend fun generateText(apiKey: String, prompt: String): String {
        return withContext(Dispatchers.IO) {
            val connection = (URL(ENDPOINT).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = CONNECT_TIMEOUT_MILLIS
                readTimeout = READ_TIMEOUT_MILLIS
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("x-goog-api-key", apiKey)
            }

            val requestBody = buildRequestBody(prompt)
            connection.outputStream.use { outputStream ->
                outputStream.write(requestBody.toByteArray(StandardCharsets.UTF_8))
            }

            val responseCode = connection.responseCode
            val stream = if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream ?: connection.inputStream
            }
            val responseBody = stream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
            connection.disconnect()

            if (responseCode !in 200..299) {
                error("Gemini request failed with HTTP $responseCode: $responseBody")
            }

            GeminiGenerateContentResponseParser.parseText(responseBody)
        }
    }

    private fun buildRequestBody(prompt: String): String {
        val encodedPrompt = JsonStringCodec.encode(prompt)
        return """
            {
              "contents": [
                {
                  "parts": [
                    { "text": "$encodedPrompt" }
                  ]
                }
              ],
              "generationConfig": {
                "temperature": 0.2,
                "responseMimeType": "application/json"
              }
            }
        """.trimIndent()
    }

    private companion object {
        const val ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"
        const val CONNECT_TIMEOUT_MILLIS = 10_000
        const val READ_TIMEOUT_MILLIS = 20_000
    }
}
