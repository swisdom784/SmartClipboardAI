package com.smartclipboard.ai.export.calendar

object SamsungCalendarPackagePolicy {
    val preferredPackages = listOf(
        "com.samsung.android.calendar",
        "com.samsung.android.app.calendar"
    )

    fun choosePreferredPackage(installedPackages: Set<String>): String? {
        return preferredPackages.firstOrNull { it in installedPackages }
    }
}
