package com.smartclipboard.ai.export.calendar

import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract

data class SamsungCalendarInsertIntentSpec(
    val action: String,
    val dataUri: String,
    val title: String,
    val description: String,
    val location: String,
    val beginTimeMs: Long,
    val endTimeMs: Long,
    val isAllDay: Boolean,
    val packageName: String?
)

object SamsungCalendarInsertIntentFactory {
    private const val CALENDAR_EVENTS_URI = "content://com.android.calendar/events"

    fun createSpec(
        title: String,
        description: String,
        location: String?,
        beginTimeMs: Long?,
        endTimeMs: Long?,
        isAllDay: Boolean,
        packageName: String?
    ): SamsungCalendarInsertIntentSpec? {
        val start = beginTimeMs ?: return null
        val end = endTimeMs ?: return null
        if (start >= end) {
            return null
        }

        return SamsungCalendarInsertIntentSpec(
            action = Intent.ACTION_INSERT,
            dataUri = CALENDAR_EVENTS_URI,
            title = title.trim().ifBlank { "새 일정" },
            description = description.trim(),
            location = location.orEmpty().trim(),
            beginTimeMs = start,
            endTimeMs = end,
            isAllDay = isAllDay,
            packageName = packageName
        )
    }

    fun createIntent(spec: SamsungCalendarInsertIntentSpec): Intent {
        return Intent(spec.action).apply {
            data = Uri.parse(spec.dataUri)
            putExtra(CalendarContract.Events.TITLE, spec.title)
            putExtra(CalendarContract.Events.DESCRIPTION, spec.description)
            putExtra(CalendarContract.Events.EVENT_LOCATION, spec.location)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, spec.beginTimeMs)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, spec.endTimeMs)
            putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, spec.isAllDay)
            spec.packageName?.let(::setPackage)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}
