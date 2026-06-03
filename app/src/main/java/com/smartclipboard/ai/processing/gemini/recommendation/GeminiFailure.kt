package com.smartclipboard.ai.processing.gemini.recommendation

sealed interface GeminiFailure {
    data object MissingApiKey : GeminiFailure
    data object InvalidApiKey : GeminiFailure
    data object NetworkFailure : GeminiFailure

    data class ApiFailure(
        val httpCode: Int,
        val message: String
    ) : GeminiFailure

    data class ParseFailure(
        val message: String
    ) : GeminiFailure
}

class GeminiRequestException(
    val failure: GeminiFailure,
    detailMessage: String,
    cause: Throwable? = null
) : IllegalStateException(detailMessage, cause)

object GeminiFailureClassifier {
    fun classify(
        httpCode: Int,
        responseBody: String
    ): GeminiFailure {
        return when {
            responseBody.contains("API_KEY_INVALID", ignoreCase = true) ||
                responseBody.contains("API key not valid", ignoreCase = true) ->
                GeminiFailure.InvalidApiKey

            else -> GeminiFailure.ApiFailure(
                httpCode = httpCode,
                message = "Gemini 요청을 완료하지 못했어요"
            )
        }
    }
}

fun Throwable.toGeminiFailureOrNull(): GeminiFailure? {
    return when (this) {
        is GeminiApiKeyMissingException -> GeminiFailure.MissingApiKey
        is GeminiRequestException -> failure
        else -> null
    }
}
