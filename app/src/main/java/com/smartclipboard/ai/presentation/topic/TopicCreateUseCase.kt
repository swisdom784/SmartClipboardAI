package com.smartclipboard.ai.presentation.topic

import com.smartclipboard.ai.domain.model.Topic
import com.smartclipboard.ai.domain.model.TopicItemSelectedBy
import com.smartclipboard.ai.domain.model.TopicOrigin
import com.smartclipboard.ai.domain.model.TopicStatus
import com.smartclipboard.ai.domain.repository.DataRepository
import javax.inject.Inject

class TopicCreateUseCase @Inject constructor(
    private val repository: DataRepository
) {
    private var nowMillis: () -> Long = { System.currentTimeMillis() }

    internal constructor(
        repository: DataRepository,
        nowMillis: () -> Long
    ) : this(repository) {
        this.nowMillis = nowMillis
    }

    suspend fun createFromUserRequest(rawPrompt: String): TopicCreateResult {
        val prompt = rawPrompt.trim()
        if (prompt.isBlank()) {
            return TopicCreateResult.Ignored
        }

        val now = nowMillis()
        val topicId = repository.createTopic(
            topic = Topic(
                title = prompt.toTopicTitle(),
                prompt = prompt,
                origin = TopicOrigin.USER_REQUEST,
                status = TopicStatus.ACTIVE,
                createdAtMillis = now,
                updatedAtMillis = now
            ),
            dataItemIds = emptyList(),
            selectedBy = TopicItemSelectedBy.USER
        )
        return TopicCreateResult.Created(topicId)
    }

    suspend fun createFromRecommendation(selection: TopicRecommendationSelection): TopicCreateResult {
        val title = selection.title.trim()
        val prompt = selection.prompt.trim()
        if (title.isBlank() || prompt.isBlank()) {
            return TopicCreateResult.Ignored
        }

        val now = nowMillis()
        val topicId = repository.createTopic(
            topic = Topic(
                title = title.toTopicTitle(),
                prompt = prompt,
                origin = TopicOrigin.AI_RECOMMENDATION,
                status = TopicStatus.ACTIVE,
                createdAtMillis = now,
                updatedAtMillis = now
            ),
            dataItemIds = selection.sourceDataItemIds.distinct(),
            selectedBy = TopicItemSelectedBy.AI
        )
        return TopicCreateResult.Created(topicId)
    }

    private fun String.toTopicTitle(): String {
        return lineSequence()
            .firstOrNull()
            ?.trim()
            ?.take(MAX_TITLE_LENGTH)
            .orEmpty()
    }

    private companion object {
        const val MAX_TITLE_LENGTH = 48
    }
}

data class TopicRecommendationSelection(
    val title: String,
    val prompt: String,
    val sourceDataItemIds: List<Long>
)

sealed interface TopicCreateResult {
    data class Created(val topicId: Long) : TopicCreateResult
    data object Ignored : TopicCreateResult
}
