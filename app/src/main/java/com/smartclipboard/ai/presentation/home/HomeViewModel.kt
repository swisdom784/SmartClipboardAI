package com.smartclipboard.ai.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartclipboard.ai.domain.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DataRepository
) : ViewModel() {
    private var hasRequestedInitialRecommendation = false

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
}
