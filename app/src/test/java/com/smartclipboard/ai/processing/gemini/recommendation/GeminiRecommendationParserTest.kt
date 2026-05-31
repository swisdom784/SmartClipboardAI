package com.smartclipboard.ai.processing.gemini.recommendation

import org.junit.Assert.assertEquals
import org.junit.Test

class GeminiRecommendationParserTest {
    @Test
    fun `parses recommendation json from fenced model text`() {
        val modelText = """
            ```json
            {
              "recommendations": [
                {
                  "title": "최근 여행 자료 정리",
                  "reason": "제주 일정 이미지와 항공권 링크가 함께 모였어요.",
                  "prompt": "제주 여행 준비 자료를 한 번에 정리해줘",
                  "dataItemIds": [10, 11, 12],
                  "confidence": 0.82
                }
              ]
            }
            ```
        """.trimIndent()

        val recommendations = GeminiRecommendationParser.parse(
            modelText = modelText,
            createdAtMillis = 1_717_500_000_000L
        )

        assertEquals(1, recommendations.size)
        assertEquals("rec_1717500000000_0", recommendations.single().id)
        assertEquals("최근 여행 자료 정리", recommendations.single().title)
        assertEquals("제주 일정 이미지와 항공권 링크가 함께 모였어요.", recommendations.single().reason)
        assertEquals("제주 여행 준비 자료를 한 번에 정리해줘", recommendations.single().prompt)
        assertEquals(listOf(10L, 11L, 12L), recommendations.single().sourceDataItemIds)
        assertEquals(0.82, recommendations.single().confidence ?: 0.0, 0.001)
    }

    @Test
    fun `drops recommendations without title or prompt`() {
        val modelText = """
            {
              "recommendations": [
                { "title": "", "reason": "자료가 적어요", "prompt": "정리해줘", "dataItemIds": [1] },
                { "title": "링크 다시 보기", "reason": "상품 링크가 있어요", "prompt": "", "dataItemIds": [2] }
              ]
            }
        """.trimIndent()

        val recommendations = GeminiRecommendationParser.parse(
            modelText = modelText,
            createdAtMillis = 1_717_500_000_000L
        )

        assertEquals(emptyList<TopicRecommendationCandidate>(), recommendations)
    }
}
