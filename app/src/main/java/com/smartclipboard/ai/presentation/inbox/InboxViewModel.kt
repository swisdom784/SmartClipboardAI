package com.smartclipboard.ai.presentation.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartclipboard.ai.domain.repository.DataRepository
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
class InboxViewModel @Inject constructor(
    private val repository: DataRepository
) : ViewModel() {
    private val selectedCategoryId = MutableStateFlow(InboxCategoryId.RECENT)
    private val viewMode = MutableStateFlow(InboxViewMode.LIST)

    val uiState: StateFlow<InboxUiState> = combine(
        repository.observeInboxItems(),
        selectedCategoryId,
        viewMode
    ) { items, categoryId, mode ->
        InboxUiStateMapper.map(
            items = items,
            selectedCategoryId = categoryId,
            viewMode = mode
        )
    }
        .catch { emit(InboxUiState()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = InboxUiState()
        )

    fun selectCategory(categoryId: InboxCategoryId) {
        selectedCategoryId.value = categoryId
    }

    fun toggleViewMode() {
        viewMode.value = when (viewMode.value) {
            InboxViewMode.LIST -> InboxViewMode.GRID
            InboxViewMode.GRID -> InboxViewMode.LIST
        }
    }

    fun toggleImportant(itemId: Long) {
        viewModelScope.launch {
            val item = repository.getDataItem(itemId) ?: return@launch
            repository.saveDataItem(
                item.copy(
                    storage = item.storage.copy(isImportant = !item.storage.isImportant),
                    updatedAtMillis = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteItem(itemId: Long) {
        viewModelScope.launch {
            val item = repository.getDataItem(itemId) ?: return@launch
            val now = System.currentTimeMillis()
            repository.saveDataItem(
                item.copy(
                    updatedAtMillis = now,
                    deletedAtMillis = now
                )
            )
        }
    }
}
