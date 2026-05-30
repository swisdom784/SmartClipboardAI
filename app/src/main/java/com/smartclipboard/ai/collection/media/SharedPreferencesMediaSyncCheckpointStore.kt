package com.smartclipboard.ai.collection.media

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferencesMediaSyncCheckpointStore @Inject constructor(
    @ApplicationContext context: Context
) : MediaSyncCheckpointStore {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override var lastSyncMillis: Long?
        get() {
            val value = preferences.getLong(KEY_LAST_SYNC_MILLIS, NO_VALUE)
            return if (value == NO_VALUE) null else value
        }
        set(value) {
            preferences.edit()
                .putLong(KEY_LAST_SYNC_MILLIS, value ?: NO_VALUE)
                .apply()
        }

    private companion object {
        const val PREFERENCES_NAME = "smart_clipboard_media_sync"
        const val KEY_LAST_SYNC_MILLIS = "last_sync_millis"
        const val NO_VALUE = -1L
    }
}
