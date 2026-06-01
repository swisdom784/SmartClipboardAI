package com.smartclipboard.ai.export.notes

import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SamsungNotesShareIntentFactoryTest {
    @Test
    fun createsTextPlainShareSpecForSamsungNotes() {
        val spec = SamsungNotesShareIntentFactory.createSpec(
            title = "요약 노트",
            body = "출장 준비 자료를 정리했습니다.\n사용된 자료 2개"
        )

        assertNotNull(spec)
        checkNotNull(spec)
        assertEquals(Intent.ACTION_SEND, spec.action)
        assertEquals("text/plain", spec.mimeType)
        assertEquals("com.samsung.android.app.notes", spec.packageName)
        assertEquals(
            "요약 노트\n\n출장 준비 자료를 정리했습니다.\n사용된 자료 2개",
            spec.text
        )
        assertTrue(spec.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0)
    }

    @Test
    fun preservesLongTextWithoutPreviewTruncation() {
        val longBody = "긴 본문 ".repeat(120)

        val spec = SamsungNotesShareIntentFactory.createSpec(
            title = "긴 노트",
            body = longBody
        )

        assertNotNull(spec)
        checkNotNull(spec)
        assertEquals("긴 노트\n\n${longBody.trim()}", spec.text)
    }

    @Test
    fun returnsNullWhenTitleAndBodyAreBlank() {
        val spec = SamsungNotesShareIntentFactory.createSpec(
            title = " ",
            body = "\n"
        )

        assertNull(spec)
    }
}
