package com.smartclipboard.ai.presentation.settings

import com.smartclipboard.ai.storage.StorageUsageSummary
import kotlin.math.roundToInt

object SettingsUiStateMapper {
    fun map(
        settings: SmartClipboardSettings,
        storageUsage: StorageUsageSummary,
        permissionState: SettingsPermissionState
    ): SettingsUiState {
        return SettingsUiState(
            selectedCollectionWindowLabel = settings.collectionWindow.label,
            customHours = settings.customHours,
            collectionWindowOptions = CollectionWindowOption.entries.map { option ->
                CollectionWindowOptionItem(
                    option = option,
                    label = option.label,
                    isSelected = option == settings.collectionWindow
                )
            },
            storage = storageUsage.toStorageUi(settings.quotaBytes),
            permission = permissionState.toPermissionUi()
        )
    }

    private fun StorageUsageSummary.toStorageUi(settingsQuotaBytes: Long): SettingsStorageUi {
        val quota = quotaBytes.takeIf { it > 0L } ?: settingsQuotaBytes
        val usedPercent = if (quota <= 0L) {
            0
        } else {
            ((usedBytes.toDouble() / quota.toDouble()) * 100.0).roundToInt().coerceIn(0, 100)
        }
        val overQuota = overQuotaBytes > 0L

        return SettingsStorageUi(
            title = "${usedBytes.toReadableBytes()} / ${quota.toReadableBytes()}",
            subtitle = "자료 ${itemCount}개",
            caption = if (overQuota) "${overQuotaBytes.toReadableBytes()} 초과" else "여유 있음",
            usedPercent = usedPercent,
            isOverQuota = overQuota,
            quotaOptions = defaultQuotaOptions(selectedBytes = quota)
        )
    }

    private fun SettingsPermissionState.toPermissionUi(): SettingsPermissionUi {
        return if (isGranted) {
            SettingsPermissionUi(
                title = "권한 허용됨",
                subtitle = "새 이미지와 스크린샷을 확인할 수 있습니다.",
                showAction = false
            )
        } else {
            SettingsPermissionUi(
                title = "권한 필요",
                subtitle = requiredPermissions.joinToString(", ").ifBlank { "이미지 접근 권한" },
                showAction = true
            )
        }
    }
}

fun Long.toReadableBytes(): String {
    val kib = 1024.0
    val mib = kib * 1024.0
    val gib = mib * 1024.0
    return when {
        this < 1024L -> "$this B"
        this < 1024L * 1024L -> "${(this / kib).roundToInt()} KB"
        this < 1024L * 1024L * 1024L -> "${(this / mib).roundToInt()} MB"
        else -> {
            val value = this / gib
            if (value % 1.0 == 0.0) "${value.toInt()} GB" else "%.1f GB".format(value)
        }
    }
}
