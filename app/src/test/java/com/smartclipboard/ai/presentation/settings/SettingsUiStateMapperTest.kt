package com.smartclipboard.ai.presentation.settings

import com.smartclipboard.ai.processing.gemini.recommendation.RecommendationSession
import com.smartclipboard.ai.processing.gemini.recommendation.RecommendationSessionStatus
import com.smartclipboard.ai.storage.StorageUsageSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsUiStateMapperTest {
    @Test
    fun mapsStorageUsagePermissionAndCollectionWindow() {
        val state = SettingsUiStateMapper.map(
            settings = SmartClipboardSettings(
                collectionWindow = CollectionWindowOption.LAST_24_HOURS,
                customHours = 12,
                quotaBytes = 500L
            ),
            storageUsage = StorageUsageSummary(
                usedBytes = 375L,
                quotaBytes = 500L,
                overQuotaBytes = 0L,
                itemCount = 8
            ),
            permissionState = SettingsPermissionState(
                isGranted = false,
                requiredPermissions = listOf("android.permission.READ_MEDIA_IMAGES")
            )
        )

        assertEquals("24시간", state.selectedCollectionWindowLabel)
        assertEquals("375 B / 500 B", state.storage.title)
        assertEquals(75, state.storage.usedPercent)
        assertFalse(state.storage.isOverQuota)
        assertEquals("자료 8개", state.storage.subtitle)
        assertEquals("권한 필요", state.permission.title)
        assertTrue(state.permission.showAction)
        assertEquals(5, state.collectionWindowOptions.size)
    }

    @Test
    fun calculatesCheckpointForCustomCollectionWindow() {
        val checkpoint = CollectionWindowPolicy.checkpointMillisFor(
            option = CollectionWindowOption.CUSTOM_HOURS,
            customHours = 6,
            nowMillis = 10_000L
        )

        assertEquals(0L, checkpoint)
    }

    @Test
    fun reportsOverQuotaStorage() {
        val state = SettingsUiStateMapper.map(
            settings = SmartClipboardSettings(
                collectionWindow = CollectionWindowOption.FOLLOW_LAST_SYNC,
                customHours = 3,
                quotaBytes = 500L
            ),
            storageUsage = StorageUsageSummary(
                usedBytes = 700L,
                quotaBytes = 500L,
                overQuotaBytes = 200L,
                itemCount = 12
            ),
            permissionState = SettingsPermissionState(
                isGranted = true,
                requiredPermissions = emptyList()
            )
        )

        assertEquals("700 B / 500 B", state.storage.title)
        assertEquals(100, state.storage.usedPercent)
        assertTrue(state.storage.isOverQuota)
        assertEquals("200 B 초과", state.storage.caption)
        assertEquals("권한 허용됨", state.permission.title)
        assertFalse(state.permission.showAction)
    }

    @Test
    fun mapsMissingGeminiKeyStatus() {
        val state = SettingsUiStateMapper.map(
            settings = SmartClipboardSettings(),
            storageUsage = StorageUsageSummary(
                usedBytes = 0L,
                quotaBytes = DEFAULT_QUOTA_BYTES,
                overQuotaBytes = 0L,
                itemCount = 0
            ),
            permissionState = SettingsPermissionState(
                isGranted = true,
                requiredPermissions = emptyList()
            ),
            geminiState = SettingsGeminiState(
                isApiKeyConfigured = false,
                recommendationSession = null
            )
        )

        assertEquals("Gemini API key 필요", state.gemini.title)
        assertEquals(
            "local.properties에 key를 설정하면 AI 추천과 분석을 사용할 수 있습니다.",
            state.gemini.subtitle
        )
    }

    @Test
    fun mapsInvalidGeminiKeyStatusFromLastRecommendationSession() {
        val state = SettingsUiStateMapper.map(
            settings = SmartClipboardSettings(),
            storageUsage = StorageUsageSummary(
                usedBytes = 0L,
                quotaBytes = DEFAULT_QUOTA_BYTES,
                overQuotaBytes = 0L,
                itemCount = 0
            ),
            permissionState = SettingsPermissionState(
                isGranted = true,
                requiredPermissions = emptyList()
            ),
            geminiState = SettingsGeminiState(
                isApiKeyConfigured = true,
                recommendationSession = RecommendationSession(
                    id = "failed",
                    status = RecommendationSessionStatus.FAILED,
                    recommendations = emptyList(),
                    createdAtMillis = 1L,
                    message = "Gemini API key를 확인해 주세요"
                )
            )
        )

        assertEquals("Gemini 연결 확인 필요", state.gemini.title)
        assertEquals("Gemini API key를 확인해 주세요", state.gemini.subtitle)
    }
}
