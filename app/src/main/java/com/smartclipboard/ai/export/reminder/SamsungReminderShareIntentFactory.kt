package com.smartclipboard.ai.export.reminder

import android.content.Intent

data class SamsungReminderShareIntentSpec(
    val action: String,
    val mimeType: String,
    val packageName: String,
    val text: String,
    val flags: Int
)

object SamsungReminderShareIntentFactory {
    const val SAMSUNG_REMINDER_PACKAGE = "com.samsung.android.app.reminder"
    private const val TEXT_PLAIN = "text/plain"

    fun createSpec(
        title: String,
        body: String
    ): SamsungReminderShareIntentSpec? {
        val text = listOf(title.trim(), body.trim())
            .filter { it.isNotBlank() }
            .joinToString(separator = "\n\n")

        if (text.isBlank()) {
            return null
        }

        return SamsungReminderShareIntentSpec(
            action = Intent.ACTION_SEND,
            mimeType = TEXT_PLAIN,
            packageName = SAMSUNG_REMINDER_PACKAGE,
            text = text,
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        )
    }

    fun createIntent(spec: SamsungReminderShareIntentSpec): Intent {
        return Intent(spec.action).apply {
            type = spec.mimeType
            putExtra(Intent.EXTRA_TEXT, spec.text)
            setPackage(spec.packageName)
            addFlags(spec.flags)
        }
    }
}
