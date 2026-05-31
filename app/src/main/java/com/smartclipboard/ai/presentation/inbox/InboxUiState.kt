package com.smartclipboard.ai.presentation.inbox

data class InboxUiState(
    val categories: List<InboxCategoryItem> = emptyList(),
    val selectedCategoryId: InboxCategoryId = InboxCategoryId.RECENT,
    val viewMode: InboxViewMode = InboxViewMode.LIST,
    val visibleItems: List<InboxDataItem> = emptyList()
) {
    val selectedCategoryTitle: String
        get() = categories.firstOrNull { it.id == selectedCategoryId }?.title ?: "최근"
}

data class InboxCategoryItem(
    val id: InboxCategoryId,
    val title: String,
    val subtitle: String,
    val count: Int,
    val isSelected: Boolean
)

enum class InboxCategoryId(val label: String) {
    RECENT("최근"),
    IMAGES("이미지"),
    LINKS("링크"),
    TEXTS("텍스트"),
    FILES("파일"),
    IMPORTANT("중요"),
    PENDING_ANALYSIS("미분석")
}

enum class InboxViewMode(val label: String) {
    LIST("리스트"),
    GRID("그리드")
}

data class InboxDataItem(
    val id: Long,
    val title: String,
    val description: String,
    val meta: String,
    val typeLabel: String,
    val isImportant: Boolean,
    val isAnalysisPending: Boolean
)
