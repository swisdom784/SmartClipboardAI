package com.smartclipboard.ai.export.reminder

import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SamsungReminderShareIntentFactoryTest {
    @Test
    fun createsTextPlainShareSpecForSamsungReminder() {
        val spec = SamsungReminderShareIntentFactory.createSpec(
            title = "여행 준비",
            body = "- 여권 확인\n- 충전기 챙기기"
        )

        assertNotNull(spec)
        checkNotNull(spec)
        assertEquals(Intent.ACTION_SEND, spec.action)
        assertEquals("text/plain", spec.mimeType)
        assertEquals("com.samsung.android.app.reminder", spec.packageName)
        assertEquals("여행 준비\n\n- 여권 확인\n- 충전기 챙기기", spec.text)
        assertTrue(spec.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0)
    }

    @Test
    fun preservesMultilineTaskTextWithoutPreviewTruncation() {
        val body = (1..40).joinToString(separator = "\n") { index -> "- 준비물 $index" }

        val spec = SamsungReminderShareIntentFactory.createSpec(
            title = "긴 할 일",
            body = body
        )

        assertNotNull(spec)
        checkNotNull(spec)
        assertEquals("긴 할 일\n\n$body", spec.text)
    }

    @Test
    fun returnsNullWhenTitleAndBodyAreBlank() {
        val spec = SamsungReminderShareIntentFactory.createSpec(
            title = " ",
            body = "\n"
        )

        assertNull(spec)
    }
}
