package com.smartclipboard.ai.export.calendar

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SamsungCalendarPackagePolicyTest {
    @Test
    fun prefersSamsungCalendarPackageInStableOrder() {
        val packageName = SamsungCalendarPackagePolicy.choosePreferredPackage(
            installedPackages = setOf(
                "com.samsung.android.app.calendar",
                "com.samsung.android.calendar"
            )
        )

        assertEquals("com.samsung.android.calendar", packageName)
    }

    @Test
    fun fallsBackToNullWhenSamsungCalendarIsNotInstalled() {
        val packageName = SamsungCalendarPackagePolicy.choosePreferredPackage(
            installedPackages = setOf("com.google.android.calendar")
        )

        assertNull(packageName)
    }
}
