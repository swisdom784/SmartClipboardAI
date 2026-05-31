package com.smartclipboard.ai.presentation.logs

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

@HiltViewModel
class LogsViewModel @Inject constructor(
    repository: DataRepository
) : ViewModel() {
    private val selectedFilter = MutableStateFlow(LogFilterId.ALL)

    val uiState: StateFlow<LogsUiState> = combine(
        repository.observeTopics(),
        selectedFilter
    ) { topics, filter ->
        LogsUiStateMapper.map(
            topics = topics,
            selectedFilter = filter
        )
    }
        .catch { emit(LogsUiState()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = LogsUiState()
        )

    fun selectFilter(filterId: LogFilterId) {
        selectedFilter.value = filterId
    }
}
