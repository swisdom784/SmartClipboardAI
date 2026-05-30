package com.smartclipboard.ai.collection.clipboard

data class ClipboardPayload(
    val text: String?,
    val hasUnsupportedContent: Boolean = false
)

sealed interface ClipboardCaptureResult {
    data class Success(val savedCount: Int) : ClipboardCaptureResult
    data class Failure(val reason: ClipboardFailureReason) : ClipboardCaptureResult
}

enum class ClipboardFailureReason {
    EmptyClipboard,
    UnsupportedContent,
    SaveFailed
}
