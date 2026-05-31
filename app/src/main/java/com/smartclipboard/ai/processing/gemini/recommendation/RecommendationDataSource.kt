package com.smartclipboard.ai.processing.gemini.recommendation

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.repository.DataRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

interface RecommendationDataSource {
    suspend fun getRecommendationInputItems(limit: Int): List<DataItem>
}

@Singleton
class RepositoryRecommendationDataSource @Inject constructor(
    private val repository: DataRepository
) : RecommendationDataSource {
    override suspend fun getRecommendationInputItems(limit: Int): List<DataItem> {
        return repository.observeDataItems()
            .first()
            .filter { it.hasRecommendationSignal() }
            .take(limit)
    }

    private fun DataItem.hasRecommendationSignal(): Boolean {
        return when (type) {
            DataItemType.TEXT -> !textContent.isNullOrBlank()
            DataItemType.LINK -> !sourceUri.isNullOrBlank() || !textContent.isNullOrBlank()
            DataItemType.IMAGE,
            DataItemType.SCREENSHOT,
            DataItemType.DOWNLOAD_IMAGE -> !sourceUri.isNullOrBlank() || !enrichment.ocrText.isNullOrBlank()
            DataItemType.FILE -> !displayName.isNullOrBlank() || !sourceUri.isNullOrBlank()
        }
    }
}
