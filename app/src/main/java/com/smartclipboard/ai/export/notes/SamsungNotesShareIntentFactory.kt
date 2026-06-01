package com.smartclipboard.ai.export.notes

import android.content.Intent

data class SamsungNotesShareIntentSpec(
    val action: String,
    val mimeType: String,
    val packageName: String,
    val text: String,
    val flags: Int
)

object SamsungNotesShareIntentFactory {
    const val SAMSUNG_NOTES_PACKAGE = "com.samsung.android.app.notes"
    private const val TEXT_PLAIN = "text/plain"

    fun createSpec(
        title: String,
        body: String
    ): SamsungNotesShareIntentSpec? {
        val text = listOf(title.trim(), body.trim())
            .filter { it.isNotBlank() }
            .joinToString(separator = "\n\n")

        if (text.isBlank()) {
            return null
        }

        return SamsungNotesShareIntentSpec(
            action = Intent.ACTION_SEND,
            mimeType = TEXT_PLAIN,
            packageName = SAMSUNG_NOTES_PACKAGE,
            text = text,
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        )
    }

    fun createIntent(spec: SamsungNotesShareIntentSpec): Intent {
        return Intent(spec.action).apply {
            type = spec.mimeType
            putExtra(Intent.EXTRA_TEXT, spec.text)
            setPackage(spec.packageName)
            addFlags(spec.flags)
        }
    }
}
