package com.smartclipboard.ai.presentation.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class TopicAnalysisViewModel @Inject constructor(
    private val repository: DataRepository,
    private val analysisUseCase: TopicAnalysisUseCase
) : ViewModel() {
    private val topicId = MutableStateFlow(0L)
    private val startedTopicIds = mutableSetOf<Long>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<TopicAnalysisUiState> = topicId
        .flatMapLatest { currentTopicId ->
            if (currentTopicId > 0L) {
                repository.observeTopicAnalyses(currentTopicId)
                    .map { analyses ->
                        TopicAnalysisUiStateMapper.map(
                            topicId = currentTopicId,
                            analyses = analyses
                        )
                    }
            } else {
                flowOf(TopicAnalysisUiState())
            }
        }
        .catch { emit(TopicAnalysisUiState(topicId = topicId.value)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = TopicAnalysisUiState()
        )

    fun start(topicId: Long) {
        if (topicId <= 0L) {
            return
        }
        this.topicId.value = topicId
        if (startedTopicIds.add(topicId)) {
            runAnalysis()
        }
    }

    fun retry() {
        runAnalysis()
    }

    private fun runAnalysis() {
        val currentTopicId = topicId.value
        if (currentTopicId <= 0L || uiState.value.isRunning) {
            return
        }
        viewModelScope.launch {
            analysisUseCase.generate(currentTopicId)
        }
    }
}
