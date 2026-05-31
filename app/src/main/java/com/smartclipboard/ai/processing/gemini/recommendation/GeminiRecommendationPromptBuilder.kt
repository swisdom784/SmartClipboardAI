package com.smartclipboard.ai.processing.gemini.recommendation

import com.smartclipboard.ai.domain.model.DataItem
import javax.inject.Inject

class GeminiRecommendationPromptBuilder @Inject constructor() {
    fun build(items: List<DataItem>): String {
        val itemLines = items
            .take(MAX_ITEMS)
            .joinToString(separator = "\n") { item -> item.toPromptLine() }

        return """
            너는 SmartClipboard의 조용한 정리 비서야.
            사용자가 방금 모은 자료를 보고, 이번 실행에서만 보여줄 작업 후보를 추천해.

            원칙:
            - "일정 만들기", "요약하기"처럼 세부 실행 버튼을 추천하지 마.
            - "최근 자료 정리", "새로 담은 링크 다시 보기", "AI 다시 분석"처럼 사용자가 흐름을 이어가기 쉬운 후보를 추천해.
            - 추천은 최대 3개만 만든다.
            - 사용자가 수락하기 전에는 영구 저장되지 않는 임시 후보라고 생각한다.
            - 반드시 JSON만 반환한다.

            출력 형식:
            {
              "recommendations": [
                {
                  "title": "최근 자료 정리",
                  "reason": "추천 이유 한 문장",
                  "prompt": "사용자가 수락하면 Topic을 만들 때 사용할 한국어 요청",
                  "dataItemIds": [1, 2],
                  "confidence": 0.0
                }
              ]
            }

            자료:
            $itemLines
        """.trimIndent()
    }

    private fun DataItem.toPromptLine(): String {
        val content = listOfNotNull(
            textContent?.let { "text=${it.compact()}" },
            sourceUri?.let { "uri=$it" },
            enrichment.ogTitle?.let { "ogTitle=${it.compact()}" },
            enrichment.ogDescription?.let { "ogDescription=${it.compact()}" },
            enrichment.ocrText?.let { "ocr=${it.compact()}" }
        ).joinToString(", ")

        return "- id=$id, type=${type.name}, source=${source.name}, capturedAt=$capturedAtMillis, $content"
    }

    private fun String.compact(): String {
        return replace(Regex("\\s+"), " ")
            .trim()
            .take(MAX_TEXT_LENGTH)
    }

    private companion object {
        const val MAX_ITEMS = 20
        const val MAX_TEXT_LENGTH = 240
    }
}
