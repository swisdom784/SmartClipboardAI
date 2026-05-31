package com.smartclipboard.ai.presentation.settings

data class SmartClipboardSettings(
    val collectionWindow: CollectionWindowOption = CollectionWindowOption.FOLLOW_LAST_SYNC,
    val customHours: Int = 24,
    val quotaBytes: Long = DEFAULT_QUOTA_BYTES
)

enum class CollectionWindowOption(val label: String) {
    FOLLOW_LAST_SYNC("마지막 종료 이후"),
    LAST_1_HOUR("1시간"),
    LAST_24_HOURS("24시간"),
    LAST_7_DAYS("7일"),
    CUSTOM_HOURS("직접 설정")
}

data class SettingsPermissionState(
    val isGranted: Boolean,
    val requiredPermissions: List<String>
)

data class SettingsUiState(
    val selectedCollectionWindowLabel: String = CollectionWindowOption.FOLLOW_LAST_SYNC.label,
    val customHours: Int = 24,
    val collectionWindowOptions: List<CollectionWindowOptionItem> = emptyList(),
    val storage: SettingsStorageUi = SettingsStorageUi(),
    val permission: SettingsPermissionUi = SettingsPermissionUi()
)

data class CollectionWindowOptionItem(
    val option: CollectionWindowOption,
    val label: String,
    val isSelected: Boolean
)

data class SettingsStorageUi(
    val title: String = "0 B / 500 MB",
    val subtitle: String = "자료 0개",
    val caption: String = "여유 있음",
    val usedPercent: Int = 0,
    val isOverQuota: Boolean = false,
    val quotaOptions: List<QuotaOptionItem> = defaultQuotaOptions()
)

data class QuotaOptionItem(
    val label: String,
    val bytes: Long,
    val isSelected: Boolean
)

data class SettingsPermissionUi(
    val title: String = "권한 확인 중",
    val subtitle: String = "",
    val actionLabel: String = "권한 허용",
    val showAction: Boolean = false
)

const val DEFAULT_QUOTA_BYTES: Long = 500L * 1024L * 1024L

fun defaultQuotaOptions(selectedBytes: Long = DEFAULT_QUOTA_BYTES): List<QuotaOptionItem> {
    return listOf(
        250L * 1024L * 1024L,
        500L * 1024L * 1024L,
        1024L * 1024L * 1024L
    ).map { bytes ->
        QuotaOptionItem(
            label = bytes.toReadableBytes(),
            bytes = bytes,
            isSelected = bytes == selectedBytes
        )
    }
}
