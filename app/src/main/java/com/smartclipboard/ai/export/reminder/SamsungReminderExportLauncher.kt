package com.smartclipboard.ai.export.reminder

import android.content.ActivityNotFoundException
import android.content.Context

object SamsungReminderExportLauncher {
    fun export(
        context: Context,
        title: String,
        body: String
    ): SamsungReminderExportResult {
        val spec = SamsungReminderShareIntentFactory.createSpec(
            title = title,
            body = body
        ) ?: return SamsungReminderExportResult.EmptyContent
        val intent = SamsungReminderShareIntentFactory.createIntent(spec)

        if (intent.resolveActivity(context.packageManager) == null) {
            return SamsungReminderExportResult.AppNotFound
        }

        return try {
            context.startActivity(intent)
            SamsungReminderExportResult.Started
        } catch (_: ActivityNotFoundException) {
            SamsungReminderExportResult.AppNotFound
        } catch (_: RuntimeException) {
            SamsungReminderExportResult.Failed
        }
    }
}

sealed interface SamsungReminderExportResult {
    data object Started : SamsungReminderExportResult
    data object AppNotFound : SamsungReminderExportResult
    data object EmptyContent : SamsungReminderExportResult
    data object Failed : SamsungReminderExportResult
}
