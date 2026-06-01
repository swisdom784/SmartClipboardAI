package com.smartclipboard.ai.presentation.analysis

import com.smartclipboard.ai.domain.model.TopicAnalysis
import com.smartclipboard.ai.domain.model.TopicAnalysisStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TopicAnalysisUiStateMapperTest {
    @Test
    fun mapsDoneAnalysisToReviewableSummary() {
        val state = TopicAnalysisUiStateMapper.map(
            topicId = 7L,
            analyses = listOf(
                analysis(
                    id = 2L,
                    status = TopicAnalysisStatus.DONE,
                    summary = "출장 준비 자료를 정리했습니다.",
                    evidence = listOf("dataItemId=1: 항공권")
                )
            )
        )

        assertEquals(7L, state.topicId)
        assertEquals("분석 완료", state.statusLabel)
        assertEquals("출장 준비 자료를 정리했습니다.", state.summary)
        assertEquals(listOf("dataItemId=1: 항공권"), state.evidence)
        assertFalse(state.canRetry)
    }

    @Test
    fun mapsFailedAnalysisToRetryState() {
        val state = TopicAnalysisUiStateMapper.map(
            topicId = 7L,
            analyses = listOf(
                analysis(
                    id = 2L,
                    status = TopicAnalysisStatus.FAILED,
                    failureReason = "Gemini API key is missing"
                )
            )
        )

        assertEquals("분석 보류", state.statusLabel)
        assertEquals("Gemini API key is missing", state.summary)
        assertTrue(state.canRetry)
    }

    private fun analysis(
        id: Long,
        status: TopicAnalysisStatus,
        summary: String? = null,
        evidence: List<String> = emptyList(),
        failureReason: String? = null
    ): TopicAnalysis {
        return TopicAnalysis(
            id = id,
            topicId = 7L,
            status = status,
            summary = summary,
            evidence = evidence,
            failureReason = failureReason,
            createdAtMillis = id,
            updatedAtMillis = id
        )
    }
}
