package com.smartclipboard.ai.export.calendar

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.pm.PackageManager

object SamsungCalendarExportLauncher {
    fun export(
        context: Context,
        title: String,
        description: String,
        location: String?,
        beginTimeMs: Long?,
        endTimeMs: Long?,
        isAllDay: Boolean
    ): SamsungCalendarExportResult {
        val packageName = SamsungCalendarPackagePolicy.choosePreferredPackage(
            installedPackages = SamsungCalendarPackagePolicy.preferredPackages
                .filter { packageName -> context.isPackageInstalled(packageName) }
                .toSet()
        )
        val spec = SamsungCalendarInsertIntentFactory.createSpec(
            title = title,
            description = description,
            location = location,
            beginTimeMs = beginTimeMs,
            endTimeMs = endTimeMs,
            isAllDay = isAllDay,
            packageName = packageName
        ) ?: return SamsungCalendarExportResult.InvalidTime
        val intent = SamsungCalendarInsertIntentFactory.createIntent(spec)

        if (intent.resolveActivity(context.packageManager) == null) {
            return SamsungCalendarExportResult.AppNotFound
        }

        return try {
            context.startActivity(intent)
            SamsungCalendarExportResult.Started
        } catch (_: ActivityNotFoundException) {
            SamsungCalendarExportResult.AppNotFound
        } catch (_: RuntimeException) {
            SamsungCalendarExportResult.Failed
        }
    }

    private fun Context.isPackageInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        } catch (_: RuntimeException) {
            false
        }
    }
}

sealed interface SamsungCalendarExportResult {
    data object Started : SamsungCalendarExportResult
    data object AppNotFound : SamsungCalendarExportResult
    data object InvalidTime : SamsungCalendarExportResult
    data object Failed : SamsungCalendarExportResult
}
