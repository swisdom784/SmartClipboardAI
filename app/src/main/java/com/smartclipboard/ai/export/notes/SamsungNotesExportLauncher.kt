package com.smartclipboard.ai.export.notes

import android.content.ActivityNotFoundException
import android.content.Context

object SamsungNotesExportLauncher {
    fun export(
        context: Context,
        title: String,
        body: String
    ): SamsungNotesExportResult {
        val spec = SamsungNotesShareIntentFactory.createSpec(
            title = title,
            body = body
        ) ?: return SamsungNotesExportResult.EmptyContent

        val intent = SamsungNotesShareIntentFactory.createIntent(spec)
        if (intent.resolveActivity(context.packageManager) == null) {
            return SamsungNotesExportResult.AppNotFound
        }

        return try {
            context.startActivity(intent)
            SamsungNotesExportResult.Started
        } catch (_: ActivityNotFoundException) {
            SamsungNotesExportResult.AppNotFound
        } catch (_: RuntimeException) {
            SamsungNotesExportResult.Failed
        }
    }
}

sealed interface SamsungNotesExportResult {
    data object Started : SamsungNotesExportResult
    data object AppNotFound : SamsungNotesExportResult
    data object EmptyContent : SamsungNotesExportResult
    data object Failed : SamsungNotesExportResult
}
