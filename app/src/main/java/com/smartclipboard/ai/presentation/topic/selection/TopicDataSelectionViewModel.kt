package com.smartclipboard.ai.presentation.topic.selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartclipboard.ai.domain.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class TopicDataSelectionViewModel @Inject constructor(
    private val repository: DataRepository,
    private val selectionUseCase: TopicDataSelectionUseCase
) : ViewModel() {
    private val topicId = MutableStateFlow(0L)
    private val editedSelectedIds = MutableStateFlow<Set<Long>?>(null)
    private val isSaving = MutableStateFlow(false)
    private val _selectionSavedEvents = MutableSharedFlow<Unit>()

    val selectionSavedEvents: SharedFlow<Unit> = _selectionSavedEvents.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val linkedItems = topicId.flatMapLatest { currentTopicId ->
        if (currentTopicId > 0L) {
            repository.observeDataItemsForTopic(currentTopicId)
        } else {
            flowOf(emptyList())
        }
    }

    val uiState: StateFlow<TopicDataSelectionUiState> = combine(
        topicId,
        repository.observeInboxItems(),
        linkedItems,
        editedSelectedIds,
        isSaving
    ) { currentTopicId, allItems, selectedItems, editedIds, saving ->
        val selectedIds = editedIds ?: selectedItems.map { it.id }.toSet()
        TopicDataSelectionUiStateMapper.map(
            topicId = currentTopicId,
            allItems = allItems,
            selectedDataItemIds = selectedIds,
            isSaving = saving
        )
    }
        .catch { emit(TopicDataSelectionUiState(topicId = topicId.value)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = TopicDataSelectionUiState()
        )

    fun load(topicId: Long) {
        if (this.topicId.value == topicId) {
            return
        }
        this.topicId.value = topicId
        editedSelectedIds.value = null
    }

    fun toggleItem(itemId: Long) {
        if (itemId <= 0L) {
            return
        }
        val currentIds = editedSelectedIds.value
            ?: uiState.value.items.filter { it.isSelected }.map { it.id }.toSet()
        editedSelectedIds.value = if (itemId in currentIds) {
            currentIds - itemId
        } else {
            currentIds + itemId
        }
    }

    fun saveSelection() {
        val currentTopicId = topicId.value
        val selectedIds = uiState.value.items
            .filter { it.isSelected }
            .map { it.id }

        viewModelScope.launch {
            isSaving.value = true
            val result = runCatching {
                selectionUseCase.saveUserSelection(
                    topicId = currentTopicId,
                    selectedDataItemIds = selectedIds
                )
            }.getOrNull()
            isSaving.value = false
            if (result is TopicDataSelectionSaveResult.Saved) {
                editedSelectedIds.value = selectedIds.toSet()
                _selectionSavedEvents.emit(Unit)
            }
        }
    }
}
