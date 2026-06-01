package com.smartclipboard.ai.presentation.topic.selection

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.EnrichmentStatus

data class TopicDataSelectionUiState(
    val topicId: Long = 0L,
    val summary: TopicDataSelectionSummary = TopicDataSelectionSummary(
        title = "사용된 자료 0개",
        subtitle = "분석에 사용할 자료를 선택하세요."
    ),
    val items: List<TopicSelectableDataItem> = emptyList(),
    val isSaving: Boolean = false
)

data class TopicSelectableDataItem(
    val id: Long,
    val title: String,
    val description: String,
    val meta: String,
    val typeLabel: String,
    val isSelected: Boolean
)

object TopicDataSelectionUiStateMapper {
    private const val MAX_SELECTABLE_ITEMS = 200

    fun map(
        topicId: Long,
        allItems: List<DataItem>,
        selectedDataItemIds: Set<Long>,
        isSaving: Boolean = false
    ): TopicDataSelectionUiState {
        val normalizedSelectedIds = selectedDataItemIds.filter { it > 0L }.toSet()
        val selectedItems = allItems.filter { it.id in normalizedSelectedIds }
        val selectableItems = allItems
            .sortedWith(
                compareByDescending<DataItem> { it.id in normalizedSelectedIds }
                    .thenByDescending { it.capturedAtMillis }
                    .thenByDescending { it.id }
            )
            .take(MAX_SELECTABLE_ITEMS)
            .map { item ->
                item.toSelectableDataItem(
                    isSelected = item.id in normalizedSelectedIds
                )
            }

        return TopicDataSelectionUiState(
            topicId = topicId,
            summary = TopicDataSelectionSummaryMapper.summarize(selectedItems),
            items = selectableItems,
            isSaving = isSaving
        )
    }

    private fun DataItem.toSelectableDataItem(isSelected: Boolean): TopicSelectableDataItem {
        val typeLabel = type.label()
        return TopicSelectableDataItem(
            id = id,
            title = title(),
            description = description(),
            meta = "$typeLabel · ${source.label()}",
            typeLabel = typeLabel,
            isSelected = isSelected
        )
    }

    private fun DataItem.title(): String {
        return when {
            enrichment.ogTitle?.isNotBlank() == true -> enrichment.ogTitle
            displayName?.isNotBlank() == true -> displayName
            textContent?.isNotBlank() == true -> textContent.lineSequence().first().take(80)
            sourceUri?.isNotBlank() == true -> sourceUri
            else -> type.label()
        }
    }

    private fun DataItem.description(): String {
        return when {
            enrichment.ogDescription?.isNotBlank() == true -> enrichment.ogDescription
            enrichment.ocrText?.isNotBlank() == true -> enrichment.ocrText
            textContent?.isNotBlank() == true -> textContent
            mimeType?.isNotBlank() == true -> mimeType
            else -> when (enrichment.status) {
                EnrichmentStatus.DONE -> "분석 완료"
                EnrichmentStatus.FAILED -> "분석 보류"
                EnrichmentStatus.PROCESSING -> "분석 중"
                EnrichmentStatus.PENDING -> "분석 대기"
            }
        }
    }

    private fun DataItemType.label(): String {
        return when (this) {
            DataItemType.TEXT -> "텍스트"
            DataItemType.LINK -> "링크"
            DataItemType.IMAGE,
            DataItemType.SCREENSHOT,
            DataItemType.DOWNLOAD_IMAGE -> "이미지"
            DataItemType.FILE -> "파일"
        }
    }

    private fun DataItemSource.label(): String {
        return when (this) {
            DataItemSource.SHARE_TARGET -> "공유"
            DataItemSource.CLIPBOARD_TILE -> "클립보드"
            DataItemSource.MEDIASTORE -> "갤러리"
            DataItemSource.SAF -> "파일 선택"
            DataItemSource.MANUAL -> "직접 추가"
        }
    }
}

object TopicDataSelectionSummaryMapper {
    fun summarize(selectedItems: List<DataItem>): TopicDataSelectionSummary {
        return TopicDataSelectionSummary(
            title = "사용된 자료 ${selectedItems.size}개",
            subtitle = selectedItems.typeSummary()
        )
    }

    private fun List<DataItem>.typeSummary(): String {
        if (isEmpty()) {
            return "분석에 사용할 자료를 선택하세요."
        }

        return listOfNotNull(
            count { it.type.isImageLike() }.labelPart("이미지"),
            count { it.type == DataItemType.LINK }.labelPart("링크"),
            count { it.type == DataItemType.TEXT }.labelPart("텍스트"),
            count { it.type == DataItemType.FILE }.labelPart("파일")
        ).joinToString(" · ")
    }

    private fun Int.labelPart(label: String): String? {
        return takeIf { it > 0 }?.let { "$label $it" }
    }

    private fun DataItemType.isImageLike(): Boolean {
        return this == DataItemType.IMAGE ||
            this == DataItemType.SCREENSHOT ||
            this == DataItemType.DOWNLOAD_IMAGE
    }
}
