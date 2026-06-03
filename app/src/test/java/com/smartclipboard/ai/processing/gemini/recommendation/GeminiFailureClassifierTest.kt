package com.smartclipboard.ai.processing.gemini.recommendation

import org.junit.Assert.assertEquals
import org.junit.Test

class GeminiFailureClassifierTest {
    @Test
    fun classifiesInvalidApiKeyErrorBody() {
        val errorBody = """
            {
              "error": {
                "code": 400,
                "message": "API key not valid. Please pass a valid API key.",
                "status": "INVALID_ARGUMENT",
                "details": [
                  {
                    "reason": "API_KEY_INVALID",
                    "domain": "googleapis.com"
                  }
                ]
              }
            }
        """.trimIndent()

        val failure = GeminiFailureClassifier.classify(
            httpCode = 400,
            responseBody = errorBody
        )

        assertEquals(GeminiFailure.InvalidApiKey, failure)
    }

    @Test
    fun classifiesGenericHttpFailureWithoutKeepingRawBody() {
        val failure = GeminiFailureClassifier.classify(
            httpCode = 503,
            responseBody = """{"error":{"message":"backend unavailable"}}"""
        )

        assertEquals(
            GeminiFailure.ApiFailure(
                httpCode = 503,
                message = "Gemini 요청을 완료하지 못했어요"
            ),
            failure
        )
    }
}
