package com.smartclipboard.ai.presentation.settings

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class SettingsPreferencesStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val _settings = MutableStateFlow(readSettings())

    val settings: StateFlow<SmartClipboardSettings> = _settings.asStateFlow()
    val current: SmartClipboardSettings
        get() = _settings.value

    fun updateCollectionWindow(
        option: CollectionWindowOption,
        customHours: Int = current.customHours
    ) {
        update(
            current.copy(
                collectionWindow = option,
                customHours = customHours.coerceAtLeast(1)
            )
        )
    }

    fun updateQuotaBytes(quotaBytes: Long) {
        update(current.copy(quotaBytes = quotaBytes.coerceAtLeast(1L)))
    }

    private fun update(settings: SmartClipboardSettings) {
        preferences.edit()
            .putString(KEY_COLLECTION_WINDOW, settings.collectionWindow.name)
            .putInt(KEY_CUSTOM_HOURS, settings.customHours)
            .putLong(KEY_QUOTA_BYTES, settings.quotaBytes)
            .apply()
        _settings.value = settings
    }

    private fun readSettings(): SmartClipboardSettings {
        return SmartClipboardSettings(
            collectionWindow = preferences.getString(KEY_COLLECTION_WINDOW, null)
                ?.let { runCatching { CollectionWindowOption.valueOf(it) }.getOrNull() }
                ?: CollectionWindowOption.FOLLOW_LAST_SYNC,
            customHours = preferences.getInt(KEY_CUSTOM_HOURS, 24).coerceAtLeast(1),
            quotaBytes = preferences.getLong(KEY_QUOTA_BYTES, DEFAULT_QUOTA_BYTES).coerceAtLeast(1L)
        )
    }

    private companion object {
        const val PREFERENCES_NAME = "smart_clipboard_settings"
        const val KEY_COLLECTION_WINDOW = "collection_window"
        const val KEY_CUSTOM_HOURS = "custom_hours"
        const val KEY_QUOTA_BYTES = "quota_bytes"
    }
}
