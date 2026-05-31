package com.smartclipboard.ai.presentation.topic.selection

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.TopicItemSelectedBy
import com.smartclipboard.ai.domain.repository.DataRepository
import javax.inject.Inject

class TopicDataSelectionUseCase @Inject constructor(
    private val repository: DataRepository
) {
    fun summarize(selectedItems: List<DataItem>): TopicDataSelectionSummary {
        return TopicDataSelectionSummaryMapper.summarize(selectedItems)
    }

    suspend fun saveUserSelection(
        topicId: Long,
        selectedDataItemIds: List<Long>
    ): TopicDataSelectionSaveResult {
        if (topicId <= 0L) {
            return TopicDataSelectionSaveResult.Ignored
        }

        val distinctIds = selectedDataItemIds
            .filter { it > 0L }
            .distinct()
        repository.replaceTopicDataItems(
            topicId = topicId,
            dataItemIds = distinctIds,
            selectedBy = TopicItemSelectedBy.USER
        )
        return TopicDataSelectionSaveResult.Saved(selectedCount = distinctIds.size)
    }
}

data class TopicDataSelectionSummary(
    val title: String,
    val subtitle: String
)

sealed interface TopicDataSelectionSaveResult {
    data class Saved(val selectedCount: Int) : TopicDataSelectionSaveResult
    data object Ignored : TopicDataSelectionSaveResult
}
