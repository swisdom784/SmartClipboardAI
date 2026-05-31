package com.smartclipboard.ai.presentation.inbox

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemSource
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.EnrichmentStatus

object InboxUiStateMapper {
    fun map(
        items: List<DataItem>,
        selectedCategoryId: InboxCategoryId,
        viewMode: InboxViewMode
    ): InboxUiState {
        val categories = buildCategories(
            items = items,
            selectedCategoryId = selectedCategoryId
        )
        val visibleItems = items
            .filter { item -> item.matches(selectedCategoryId) }
            .sortedWith(compareByDescending<DataItem> { it.capturedAtMillis }.thenByDescending { it.id })
            .map { item -> item.toInboxDataItem() }

        return InboxUiState(
            categories = categories,
            selectedCategoryId = selectedCategoryId,
            viewMode = viewMode,
            visibleItems = visibleItems
        )
    }

    private fun buildCategories(
        items: List<DataItem>,
        selectedCategoryId: InboxCategoryId
    ): List<InboxCategoryItem> {
        return InboxCategoryId.entries.map { id ->
            val count = items.count { item -> item.matches(id) }
            InboxCategoryItem(
                id = id,
                title = id.label,
                subtitle = subtitleFor(id),
                count = count,
                isSelected = id == selectedCategoryId
            )
        }
    }

    private fun subtitleFor(id: InboxCategoryId): String {
        return when (id) {
            InboxCategoryId.RECENT -> "전체 자료"
            InboxCategoryId.IMAGES -> "사진과 스크린샷"
            InboxCategoryId.LINKS -> "공유 링크"
            InboxCategoryId.TEXTS -> "복사 텍스트"
            InboxCategoryId.FILES -> "선택 파일"
            InboxCategoryId.IMPORTANT -> "고정 자료"
            InboxCategoryId.PENDING_ANALYSIS -> "처리 대기"
        }
    }

    private fun DataItem.matches(categoryId: InboxCategoryId): Boolean {
        return when (categoryId) {
            InboxCategoryId.RECENT -> true
            InboxCategoryId.IMAGES -> type.isImageLike()
            InboxCategoryId.LINKS -> type == DataItemType.LINK
            InboxCategoryId.TEXTS -> type == DataItemType.TEXT
            InboxCategoryId.FILES -> type == DataItemType.FILE
            InboxCategoryId.IMPORTANT -> storage.isImportant
            InboxCategoryId.PENDING_ANALYSIS -> enrichment.status.isPendingForUser()
        }
    }

    private fun DataItem.toInboxDataItem(): InboxDataItem {
        val typeLabel = type.label()
        return InboxDataItem(
            id = id,
            title = title(),
            description = description(),
            meta = "$typeLabel · ${source.label()}",
            typeLabel = typeLabel,
            isImportant = storage.isImportant,
            isAnalysisPending = enrichment.status.isPendingForUser()
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

    private fun DataItemType.isImageLike(): Boolean {
        return this == DataItemType.IMAGE ||
            this == DataItemType.SCREENSHOT ||
            this == DataItemType.DOWNLOAD_IMAGE
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

    private fun EnrichmentStatus.isPendingForUser(): Boolean {
        return this == EnrichmentStatus.PENDING || this == EnrichmentStatus.PROCESSING
    }
}
