package com.smartclipboard.ai.presentation.home

data class HomeUiState(
    val inputPlaceholder: String = "무엇을 정리할까요?",
    val tasks: List<HomeTaskItem> = emptyList(),
    val collectionSummary: HomeCollectionSummary = HomeCollectionSummary(),
    val aiStatus: HomeAiStatus = HomeAiStatus(),
    val recentMaterials: List<HomeMaterialItem> = emptyList()
) {
    val hasReviewableRecommendations: Boolean
        get() = tasks.any { task ->
            task.kind == HomeTaskKind.RECOMMENDATION &&
                task.badges.contains(HomeTaskBadge.REVIEW_REQUIRED)
        }
}

data class HomeAiStatus(
    val title: String = "자료 0개",
    val subtitle: String = "새 자료가 들어오면 여기에 정리됩니다.",
    val tone: HomeAiStatusTone = HomeAiStatusTone.IDLE
)

enum class HomeAiStatusTone {
    IDLE,
    ACTIVE,
    WARNING
}

data class HomeTaskItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val prompt: String? = null,
    val kind: HomeTaskKind,
    val badges: List<HomeTaskBadge>,
    val sourceDataItemIds: List<Long> = emptyList()
)

enum class HomeTaskKind {
    TOPIC,
    RECOMMENDATION
}

enum class HomeTaskBadge(val label: String) {
    USER_REQUEST("사용자 요청"),
    AI_RECOMMENDATION("AI 추천"),
    IN_PROGRESS("진행 중"),
    REVIEW_REQUIRED("검토 필요"),
    COMPLETED("완료")
}

data class HomeCollectionSummary(
    val title: String = "자료 0개",
    val subtitle: String = "새 자료가 들어오면 여기에 정리됩니다."
)

data class HomeMaterialItem(
    val id: Long,
    val title: String,
    val subtitle: String,
    val type: HomeMaterialType,
    val isAnalysisPending: Boolean
)

enum class HomeMaterialType(val label: String) {
    IMAGE("이미지"),
    LINK("링크"),
    TEXT("텍스트"),
    FILE("파일")
}
