package com.smartclipboard.ai.presentation.analysis.action

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartclipboard.ai.domain.model.TopicAction
import com.smartclipboard.ai.domain.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class TopicActionDraftViewModel @Inject constructor(
    private val repository: DataRepository,
    private val actionDraftUseCase: TopicActionDraftUseCase
) : ViewModel() {
    private val topicId = MutableStateFlow(0L)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val actions: StateFlow<List<TopicAction>> = topicId
        .flatMapLatest { currentTopicId ->
            if (currentTopicId > 0L) {
                repository.observeTopicActions(currentTopicId)
            } else {
                flowOf(emptyList())
            }
        }
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<TopicActionDraftUiState> = actions
        .map(TopicActionDraftUiStateMapper::map)
        .catch { emit(TopicActionDraftUiState()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = TopicActionDraftUiState()
        )

    fun start(topicId: Long) {
        this.topicId.value = topicId
    }

    fun ensureDrafts(topicId: Long) {
        viewModelScope.launch {
            actionDraftUseCase.ensureDrafts(topicId)
        }
    }

    fun completeAction(actionId: Long) {
        val action = actions.value.firstOrNull { it.id == actionId } ?: return
        viewModelScope.launch {
            actionDraftUseCase.completeAction(action)
        }
    }

    fun markActionExported(actionId: Long) {
        val action = actions.value.firstOrNull { it.id == actionId } ?: return
        viewModelScope.launch {
            actionDraftUseCase.markActionExported(action)
        }
    }

    fun completeAll() {
        val currentTopicId = topicId.value
        if (currentTopicId <= 0L) {
            return
        }
        viewModelScope.launch {
            actionDraftUseCase.markAllCompleted(currentTopicId)
        }
    }

    fun updateActionContent(
        actionId: Long,
        title: String,
        body: String
    ) {
        val action = actions.value.firstOrNull { it.id == actionId } ?: return
        viewModelScope.launch {
            actionDraftUseCase.updateActionContent(
                action = action,
                title = title,
                body = body
            )
        }
    }
}
