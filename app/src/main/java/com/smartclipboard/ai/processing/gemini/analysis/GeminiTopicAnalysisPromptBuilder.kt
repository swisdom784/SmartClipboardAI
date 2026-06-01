package com.smartclipboard.ai.processing.gemini.analysis

import com.smartclipboard.ai.domain.model.DataItem
import javax.inject.Inject

class GeminiTopicAnalysisPromptBuilder @Inject constructor() {
    fun build(input: TopicAnalysisInput): String {
        val itemLines = input.selectedItems
            .take(MAX_ITEMS)
            .joinToString(separator = "\n") { item -> item.toPromptLine() }

        return """
            너는 SmartClipboard의 조용한 정리 비서야.
            사용자가 선택한 자료만 근거로 Topic 분석 초안을 만든다.

            원칙:
            - 사용자가 바로 검토할 수 있게 한국어로 짧고 명확하게 쓴다.
            - 자료에 없는 내용을 지어내지 않는다.
            - evidence에는 실제로 사용한 dataItemId와 근거를 남긴다.
            - 반드시 JSON만 반환한다.

            출력 형식:
            {
              "summary": "사용자가 볼 요약",
              "evidence": [
                { "dataItemId": 1, "note": "근거 한 문장" }
              ]
            }

            Topic:
            - id=${input.topic.id}
            - title=${input.topic.title.compact()}
            - prompt=${input.topic.prompt.orEmpty().compact()}

            선택 자료:
            $itemLines
        """.trimIndent()
    }

    private fun DataItem.toPromptLine(): String {
        val content = listOfNotNull(
            displayName?.let { "name=${it.compact()}" },
            textContent?.let { "text=${it.compact()}" },
            sourceUri?.let { "uri=$it" },
            enrichment.ogTitle?.let { "ogTitle=${it.compact()}" },
            enrichment.ogDescription?.let { "ogDescription=${it.compact()}" },
            enrichment.ocrText?.let { "ocr=${it.compact()}" },
            enrichment.geminiSummary?.let { "summary=${it.compact()}" }
        ).joinToString(", ")

        return "- id=$id, type=${type.name}, source=${source.name}, capturedAt=$capturedAtMillis, $content"
    }

    private fun String.compact(): String {
        return replace(Regex("\\s+"), " ")
            .trim()
            .take(MAX_TEXT_LENGTH)
    }

    private companion object {
        const val MAX_ITEMS = 30
        const val MAX_TEXT_LENGTH = 360
    }
}
