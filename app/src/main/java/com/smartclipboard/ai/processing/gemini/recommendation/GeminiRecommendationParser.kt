package com.smartclipboard.ai.processing.gemini.recommendation

object GeminiRecommendationParser {
    fun parse(
        modelText: String,
        createdAtMillis: Long
    ): List<TopicRecommendationCandidate> {
        val json = modelText.extractJsonObject() ?: return emptyList()
        val recommendationArray = json.extractArray("recommendations") ?: return emptyList()

        return recommendationArray.objectBlocks().mapIndexedNotNull { index, block ->
            val title = block.stringField("title")?.takeIf { it.isNotBlank() }
                ?: return@mapIndexedNotNull null
            val prompt = block.stringField("prompt")?.takeIf { it.isNotBlank() }
                ?: return@mapIndexedNotNull null

            TopicRecommendationCandidate(
                id = "rec_${createdAtMillis}_$index",
                title = title,
                reason = block.stringField("reason").orEmpty(),
                prompt = prompt,
                sourceDataItemIds = block.longArrayField("dataItemIds"),
                confidence = block.doubleField("confidence"),
                createdAtMillis = createdAtMillis
            )
        }
    }

    private fun String.extractJsonObject(): String? {
        val start = indexOf('{')
        val end = lastIndexOf('}')
        return if (start >= 0 && end > start) substring(start, end + 1) else null
    }

    private fun String.extractArray(fieldName: String): String? {
        val fieldIndex = indexOf("\"$fieldName\"")
        if (fieldIndex < 0) return null

        val start = indexOf('[', startIndex = fieldIndex)
        if (start < 0) return null

        var depth = 0
        for (index in start until length) {
            when (this[index]) {
                '[' -> depth += 1
                ']' -> {
                    depth -= 1
                    if (depth == 0) {
                        return substring(start + 1, index)
                    }
                }
            }
        }

        return null
    }

    private fun String.objectBlocks(): List<String> {
        val blocks = mutableListOf<String>()
        var start = -1
        var depth = 0
        var inString = false
        var escaping = false

        forEachIndexed { index, char ->
            when {
                escaping -> escaping = false
                char == '\\' && inString -> escaping = true
                char == '"' -> inString = !inString
                !inString && char == '{' -> {
                    if (depth == 0) start = index
                    depth += 1
                }
                !inString && char == '}' -> {
                    depth -= 1
                    if (depth == 0 && start >= 0) {
                        blocks += substring(start, index + 1)
                        start = -1
                    }
                }
            }
        }

        return blocks
    }

    private fun String.stringField(fieldName: String): String? {
        val pattern = Regex(
            pattern = """"$fieldName"\s*:\s*"((?:\\.|[^"\\])*)"""",
            options = setOf(RegexOption.DOT_MATCHES_ALL)
        )
        return pattern.find(this)
            ?.groupValues
            ?.get(1)
            ?.let(JsonStringCodec::decode)
            ?.trim()
    }

    private fun String.longArrayField(fieldName: String): List<Long> {
        val pattern = Regex(""""$fieldName"\s*:\s*\[([^\]]*)]""")
        return pattern.find(this)
            ?.groupValues
            ?.get(1)
            ?.split(',')
            ?.mapNotNull { it.trim().toLongOrNull() }
            ?: emptyList()
    }

    private fun String.doubleField(fieldName: String): Double? {
        val pattern = Regex(""""$fieldName"\s*:\s*(-?\d+(?:\.\d+)?)""")
        return pattern.find(this)
            ?.groupValues
            ?.get(1)
            ?.toDoubleOrNull()
    }
}
