package com.smartclipboard.ai.processing.gemini.analysis

import org.junit.Assert.assertEquals
import org.junit.Test

class GeminiTopicAnalysisParserTest {
    @Test
    fun parsesSummaryAndEvidenceFromModelJson() {
        val draft = GeminiTopicAnalysisParser.parse(
            modelText = """
                ```json
                {
                  "summary": "출장 준비 자료를 정리했습니다.",
                  "evidence": [
                    { "dataItemId": 1, "note": "항공권 예약 정보" },
                    { "dataItemId": 2, "note": "숙소 주소" }
                  ]
                }
                ```
            """.trimIndent(),
            modelName = "gemini-2.5-flash"
        )

        assertEquals("출장 준비 자료를 정리했습니다.", draft.summary)
        assertEquals(
            listOf("dataItemId=1: 항공권 예약 정보", "dataItemId=2: 숙소 주소"),
            draft.evidence
        )
        assertEquals("gemini-2.5-flash", draft.modelName)
    }
}
