package com.smartclipboard.ai.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartclipboard.ai.domain.repository.DataRepository
import com.smartclipboard.ai.presentation.topic.TopicCreateUseCase
import com.smartclipboard.ai.presentation.topic.TopicRecommendationSelection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DataRepository,
    private val topicCreateUseCase: TopicCreateUseCase
) : ViewModel() {
    private var hasRequestedInitialRecommendation = false
    private val acceptedRecommendationIds = mutableSetOf<String>()
    private val _topicCreatedEvents = MutableSharedFlow<Long>()

    val topicCreatedEvents: SharedFlow<Long> = _topicCreatedEvents.asSharedFlow()

    val uiState: StateFlow<HomeUiState> = repository.observeHomeState()
        .map(HomeUiStateMapper::map)
        .catch { emit(HomeUiState()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = HomeUiState()
        )

    fun start() {
        if (hasRequestedInitialRecommendation) {
            return
        }
        hasRequestedInitialRecommendation = true
        viewModelScope.launch {
            runCatching { repository.refreshTopicRecommendations() }
        }
    }

    fun submitUserRequest(rawPrompt: String) {
        viewModelScope.launch {
            val result = runCatching {
                topicCreateUseCase.createFromUserRequest(rawPrompt)
            }.getOrNull()
            if (result is com.smartclipboard.ai.presentation.topic.TopicCreateResult.Created) {
                _topicCreatedEvents.emit(result.topicId)
            }
        }
    }

    fun openTask(task: HomeTaskItem) {
        if (task.kind != HomeTaskKind.RECOMMENDATION) {
            return
        }
        if (!acceptedRecommendationIds.add(task.id)) {
            return
        }

        viewModelScope.launch {
            val result = runCatching {
                topicCreateUseCase.createFromRecommendation(
                    TopicRecommendationSelection(
                        title = task.title,
                        prompt = task.prompt ?: task.title,
                        sourceDataItemIds = task.sourceDataItemIds
                    )
                )
            }
            if (result.isFailure) {
                acceptedRecommendationIds.remove(task.id)
            }
            val createResult = result.getOrNull()
            if (createResult is com.smartclipboard.ai.presentation.topic.TopicCreateResult.Created) {
                _topicCreatedEvents.emit(createResult.topicId)
            }
        }
    }
}
