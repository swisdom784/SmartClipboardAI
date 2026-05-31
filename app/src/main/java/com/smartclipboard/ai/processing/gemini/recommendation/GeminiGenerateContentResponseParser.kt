package com.smartclipboard.ai.processing.gemini.recommendation

object GeminiGenerateContentResponseParser {
    private val TEXT_FIELD_PATTERN = Regex(
        pattern = """"text"\s*:\s*"((?:\\.|[^"\\])*)"""",
        options = setOf(RegexOption.DOT_MATCHES_ALL)
    )

    fun parseText(responseBody: String): String {
        val encodedText = TEXT_FIELD_PATTERN.find(responseBody)
            ?.groupValues
            ?.get(1)
            ?: error("Gemini response does not contain text")

        return JsonStringCodec.decode(encodedText)
    }
}
