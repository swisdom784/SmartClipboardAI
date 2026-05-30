package com.smartclipboard.ai.data.source.local.mapper

import com.smartclipboard.ai.data.source.local.entity.DataItemEntity
import com.smartclipboard.ai.data.source.local.entity.TopicActionEntity
import com.smartclipboard.ai.data.source.local.entity.TopicAnalysisEntity
import com.smartclipboard.ai.data.source.local.entity.TopicEntity
import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemCluster
import com.smartclipboard.ai.domain.model.DataItemEnrichment
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemStorage
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.EnrichmentStatus
import com.smartclipboard.ai.domain.model.Topic
import com.smartclipboard.ai.domain.model.TopicAction
import com.smartclipboard.ai.domain.model.TopicActionStatus
import com.smartclipboard.ai.domain.model.TopicActionTargetApp
import com.smartclipboard.ai.domain.model.TopicActionType
import com.smartclipboard.ai.domain.model.TopicAnalysis
import com.smartclipboard.ai.domain.model.TopicAnalysisStatus
import com.smartclipboard.ai.domain.model.TopicOrigin
import com.smartclipboard.ai.domain.model.TopicStatus

private const val LIST_SEPARATOR = "\n"

fun DataItemEntity.toDomain(): DataItem {
    return DataItem(
        id = id,
        type = enumOrDefault(type, DataItemType.FILE),
        source = enumOrDefault(source, DataItemSource.MANUAL),
        textContent = textContent,
        sourceUri = sourceUri,
        capturedAtMillis = capturedAtMillis,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis,
        lastSyncedAtMillis = lastSyncedAtMillis,
        mimeType = mimeType,
        displayName = displayName,
        sizeBytes = sizeBytes,
        mediaStoreId = mediaStoreId,
        storage = DataItemStorage(
            internalUri = internalUri,
            isImportant = isImportant,
            isPreserved = isPreserved
        ),
        enrichment = DataItemEnrichment(
            status = enumOrDefault(enrichmentStatus, EnrichmentStatus.PENDING),
            retryCount = enrichmentRetryCount,
            ocrText = ocrText,
            ogTitle = ogTitle,
            ogDescription = ogDescription,
            ogImageUrl = ogImageUrl,
            geminiSummary = geminiSummary,
            keywords = keywords.toStringList(),
            detectedDateTimeMillis = detectedDateTimeMillis,
            detectedLocation = detectedLocation
        ),
        cluster = clusterId?.let {
            DataItemCluster(
                id = it,
                label = clusterLabel,
                score = clusterScore,
                updatedAtMillis = clusterUpdatedAtMillis
            )
        },
        deletedAtMillis = deletedAtMillis
    )
}

fun DataItem.toEntity(): DataItemEntity {
    return DataItemEntity(
        id = id,
        type = type.name,
        source = source.name,
        textContent = textContent,
        sourceUri = sourceUri,
        internalUri = storage.internalUri,
        capturedAtMillis = capturedAtMillis,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis,
        lastSyncedAtMillis = lastSyncedAtMillis,
        mimeType = mimeType,
        displayName = displayName,
        sizeBytes = sizeBytes,
        mediaStoreId = mediaStoreId,
        enrichmentStatus = enrichment.status.name,
        enrichmentRetryCount = enrichment.retryCount,
        ocrText = enrichment.ocrText,
        ogTitle = enrichment.ogTitle,
        ogDescription = enrichment.ogDescription,
        ogImageUrl = enrichment.ogImageUrl,
        geminiSummary = enrichment.geminiSummary,
        keywords = enrichment.keywords.toStoredString(),
        detectedDateTimeMillis = enrichment.detectedDateTimeMillis,
        detectedLocation = enrichment.detectedLocation,
        clusterId = cluster?.id,
        clusterLabel = cluster?.label,
        clusterScore = cluster?.score,
        clusterUpdatedAtMillis = cluster?.updatedAtMillis,
        isImportant = storage.isImportant,
        isPreserved = storage.isPreserved,
        deletedAtMillis = deletedAtMillis
    )
}

fun TopicEntity.toDomain(): Topic {
    return Topic(
        id = id,
        title = title,
        prompt = prompt,
        origin = enumOrDefault(origin, TopicOrigin.USER_REQUEST),
        status = enumOrDefault(status, TopicStatus.ACTIVE),
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis,
        completedAtMillis = completedAtMillis
    )
}

fun Topic.toEntity(): TopicEntity {
    return TopicEntity(
        id = id,
        title = title,
        prompt = prompt,
        origin = origin.name,
        status = status.name,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis,
        completedAtMillis = completedAtMillis
    )
}

fun TopicAnalysisEntity.toDomain(): TopicAnalysis {
    return TopicAnalysis(
        id = id,
        topicId = topicId,
        status = enumOrDefault(status, TopicAnalysisStatus.PENDING),
        summary = summary,
        evidence = evidence.toStringList(),
        modelName = modelName,
        failureReason = failureReason,
        retryCount = retryCount,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis
    )
}

fun TopicAnalysis.toEntity(): TopicAnalysisEntity {
    return TopicAnalysisEntity(
        id = id,
        topicId = topicId,
        status = status.name,
        summary = summary,
        evidence = evidence.toStoredString(),
        modelName = modelName,
        failureReason = failureReason,
        retryCount = retryCount,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis
    )
}

fun TopicActionEntity.toDomain(): TopicAction {
    return TopicAction(
        id = id,
        topicId = topicId,
        analysisId = analysisId,
        type = enumOrDefault(type, TopicActionType.SUMMARY),
        status = enumOrDefault(status, TopicActionStatus.PENDING_REVIEW),
        targetApp = enumOrDefault(targetApp, TopicActionTargetApp.NONE),
        title = title,
        body = body,
        previewText = previewText,
        scheduledStartAtMillis = scheduledStartAtMillis,
        scheduledEndAtMillis = scheduledEndAtMillis,
        isAllDay = isAllDay,
        location = location,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis,
        completedAtMillis = completedAtMillis
    )
}

fun TopicAction.toEntity(): TopicActionEntity {
    return TopicActionEntity(
        id = id,
        topicId = topicId,
        analysisId = analysisId,
        type = type.name,
        status = status.name,
        targetApp = targetApp.name,
        title = title,
        body = body,
        previewText = previewText,
        scheduledStartAtMillis = scheduledStartAtMillis,
        scheduledEndAtMillis = scheduledEndAtMillis,
        isAllDay = isAllDay,
        location = location,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis,
        completedAtMillis = completedAtMillis
    )
}

private fun List<String>.toStoredString(): String? {
    return takeIf { it.isNotEmpty() }?.joinToString(LIST_SEPARATOR)
}

private fun String?.toStringList(): List<String> {
    return this
        ?.split(LIST_SEPARATOR)
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        ?: emptyList()
}

private inline fun <reified T : Enum<T>> enumOrDefault(value: String?, defaultValue: T): T {
    return value
        ?.let { runCatching { enumValueOf<T>(it) }.getOrNull() }
        ?: defaultValue
}
