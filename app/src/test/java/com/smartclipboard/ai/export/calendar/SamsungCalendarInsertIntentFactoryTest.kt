package com.smartclipboard.ai.export.calendar

import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class SamsungCalendarInsertIntentFactoryTest {
    @Test
    fun createsInsertSpecWithCalendarPayload() {
        val spec = SamsungCalendarInsertIntentFactory.createSpec(
            title = "제주도 여행",
            description = "항공권과 숙소 정보를 정리했습니다.",
            location = "제주 국제공항",
            beginTimeMs = 1_714_492_800_000L,
            endTimeMs = 1_714_579_200_000L,
            isAllDay = true,
            packageName = "com.samsung.android.calendar"
        )

        assertNotNull(spec)
        checkNotNull(spec)
        assertEquals(Intent.ACTION_INSERT, spec.action)
        assertEquals("content://com.android.calendar/events", spec.dataUri)
        assertEquals("제주도 여행", spec.title)
        assertEquals("항공권과 숙소 정보를 정리했습니다.", spec.description)
        assertEquals("제주 국제공항", spec.location)
        assertEquals(1_714_492_800_000L, spec.beginTimeMs)
        assertEquals(1_714_579_200_000L, spec.endTimeMs)
        assertEquals(true, spec.isAllDay)
        assertEquals("com.samsung.android.calendar", spec.packageName)
    }

    @Test
    fun createsFallbackSpecWithoutSamsungPackage() {
        val spec = SamsungCalendarInsertIntentFactory.createSpec(
            title = "회의",
            description = "논의할 내용",
            location = "",
            beginTimeMs = 1_000L,
            endTimeMs = 2_000L,
            isAllDay = false,
            packageName = null
        )

        assertNotNull(spec)
        checkNotNull(spec)
        assertNull(spec.packageName)
    }

    @Test
    fun rejectsInvalidTimePayload() {
        val missingStart = SamsungCalendarInsertIntentFactory.createSpec(
            title = "회의",
            description = "본문",
            location = "",
            beginTimeMs = null,
            endTimeMs = 2_000L,
            isAllDay = false,
            packageName = null
        )
        val reversed = SamsungCalendarInsertIntentFactory.createSpec(
            title = "회의",
            description = "본문",
            location = "",
            beginTimeMs = 2_000L,
            endTimeMs = 1_000L,
            isAllDay = false,
            packageName = null
        )

        assertNull(missingStart)
        assertNull(reversed)
    }
}
