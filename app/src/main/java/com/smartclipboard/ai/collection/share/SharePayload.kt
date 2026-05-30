package com.smartclipboard.ai.collection.share

enum class ShareAction {
    Send,
    SendMultiple
}

data class SharePayload(
    val action: ShareAction,
    val mimeType: String?,
    val text: String? = null,
    val streams: List<SharedUri> = emptyList()
)

data class SharedUri(
    val uri: String,
    val mimeType: String? = null,
    val displayName: String? = null,
    val sizeBytes: Long? = null
)

sealed interface ShareSaveResult {
    data class Success(val savedCount: Int) : ShareSaveResult
    data class PartialSuccess(val savedCount: Int, val skippedCount: Int) : ShareSaveResult
    data class Failure(val reason: ShareFailureReason) : ShareSaveResult
}

enum class ShareFailureReason {
    EmptyPayload,
    SaveFailed
}
