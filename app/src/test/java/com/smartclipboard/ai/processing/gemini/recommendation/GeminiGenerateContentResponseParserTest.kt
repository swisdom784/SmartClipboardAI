package com.smartclipboard.ai.processing.gemini.recommendation

import org.junit.Assert.assertEquals
import org.junit.Test

class GeminiGenerateContentResponseParserTest {
    @Test
    fun `extracts first text part from generateContent response`() {
        val response = """
            {
              "candidates": [
                {
                  "content": {
                    "parts": [
                      { "text": "{\n  \"recommendations\": []\n}" }
                    ]
                  }
                }
              ]
            }
        """.trimIndent()

        val text = GeminiGenerateContentResponseParser.parseText(response)

        assertEquals("{\n  \"recommendations\": []\n}", text)
    }
}
