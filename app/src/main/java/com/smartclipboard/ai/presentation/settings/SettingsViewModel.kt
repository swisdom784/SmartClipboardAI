package com.smartclipboard.ai.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartclipboard.ai.collection.media.SharedPreferencesMediaSyncCheckpointStore
import com.smartclipboard.ai.domain.repository.DataRepository
import com.smartclipboard.ai.storage.StorageUsageSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: DataRepository,
    private val settingsStore: SettingsPreferencesStore,
    private val checkpointStore: SharedPreferencesMediaSyncCheckpointStore,
    private val permissionReader: SettingsPermissionReader
) : ViewModel() {
    private val storageUsage = MutableStateFlow(emptyUsage(settingsStore.current.quotaBytes))
    private val permissionState = MutableStateFlow(permissionReader.readImagePermissionState())

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsStore.settings,
        storageUsage,
        permissionState
    ) { settings, usage, permission ->
        SettingsUiStateMapper.map(
            settings = settings,
            storageUsage = usage,
            permissionState = permission
        )
    }
        .catch { emit(SettingsUiState()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = SettingsUiState()
        )

    init {
        refresh()
    }

    fun refresh() {
        refreshPermissionState()
        refreshStorageUsage()
    }

    fun selectCollectionWindow(option: CollectionWindowOption) {
        val customHours = settingsStore.current.customHours
        settingsStore.updateCollectionWindow(option = option, customHours = customHours)
        applyCollectionWindow(option = option, customHours = customHours)
    }

    fun applyCustomCollectionHours(hours: Int) {
        val customHours = hours.coerceAtLeast(1)
        settingsStore.updateCollectionWindow(
            option = CollectionWindowOption.CUSTOM_HOURS,
            customHours = customHours
        )
        applyCollectionWindow(
            option = CollectionWindowOption.CUSTOM_HOURS,
            customHours = customHours
        )
    }

    fun setQuotaBytes(quotaBytes: Long) {
        settingsStore.updateQuotaBytes(quotaBytes)
        refreshStorageUsage()
    }

    fun cleanupStorage() {
        viewModelScope.launch {
            runCatching {
                repository.cleanupStorage(settingsStore.current.quotaBytes)
            }
            refreshStorageUsage()
        }
    }

    fun refreshPermissionState() {
        permissionState.value = permissionReader.readImagePermissionState()
    }

    private fun refreshStorageUsage() {
        viewModelScope.launch {
            val quotaBytes = settingsStore.current.quotaBytes
            storageUsage.value = runCatching {
                repository.getStorageUsage(quotaBytes)
            }.getOrElse {
                emptyUsage(quotaBytes)
            }
        }
    }

    private fun applyCollectionWindow(
        option: CollectionWindowOption,
        customHours: Int
    ) {
        val checkpointMillis = CollectionWindowPolicy.checkpointMillisFor(
            option = option,
            customHours = customHours,
            nowMillis = System.currentTimeMillis()
        )
        if (checkpointMillis != null) {
            checkpointStore.lastSyncMillis = checkpointMillis
        }
    }

    private companion object {
        fun emptyUsage(quotaBytes: Long): StorageUsageSummary {
            return StorageUsageSummary(
                usedBytes = 0L,
                quotaBytes = quotaBytes,
                overQuotaBytes = 0L,
                itemCount = 0
            )
        }
    }
}
