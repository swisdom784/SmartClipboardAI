package com.smartclipboard.ai.presentation.settings

import com.smartclipboard.ai.processing.gemini.recommendation.RecommendationSessionStatus
import com.smartclipboard.ai.storage.StorageUsageSummary
import kotlin.math.roundToInt

object SettingsUiStateMapper {
    fun map(
        settings: SmartClipboardSettings,
        storageUsage: StorageUsageSummary,
        permissionState: SettingsPermissionState,
        geminiState: SettingsGeminiState = SettingsGeminiState()
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
            permission = permissionState.toPermissionUi(),
            gemini = geminiState.toGeminiUi()
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

    private fun SettingsGeminiState.toGeminiUi(): SettingsGeminiUi {
        if (!isApiKeyConfigured) {
            return SettingsGeminiUi(
                title = "Gemini API key 필요",
                subtitle = "local.properties에 key를 설정하면 AI 추천과 분석을 사용할 수 있습니다.",
                needsAttention = true
            )
        }

        return when (recommendationSession?.status) {
            RecommendationSessionStatus.READY -> SettingsGeminiUi(
                title = "Gemini 준비됨",
                subtitle = "이번 실행 추천 ${recommendationSession.recommendations.size}개를 검토할 수 있습니다.",
                needsAttention = false
            )

            RecommendationSessionStatus.FAILED -> SettingsGeminiUi(
                title = "Gemini 연결 확인 필요",
                subtitle = recommendationSession.message ?: "추천과 분석을 준비하지 못했어요.",
                needsAttention = true
            )

            RecommendationSessionStatus.SKIPPED -> SettingsGeminiUi(
                title = "Gemini 대기 중",
                subtitle = recommendationSession.message ?: "새 자료가 들어오면 다시 확인합니다.",
                needsAttention = false
            )

            null -> SettingsGeminiUi(
                title = "Gemini 준비됨",
                subtitle = "AI 추천과 분석에 사용할 수 있습니다.",
                needsAttention = false
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
