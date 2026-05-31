package com.smartclipboard.ai.presentation.feedback

import com.smartclipboard.ai.collection.clipboard.ClipboardCaptureResult
import com.smartclipboard.ai.collection.clipboard.ClipboardFailureReason
import com.smartclipboard.ai.collection.share.ShareSaveResult

data class SaveFeedbackMessage(
    val text: String,
    val level: SaveFeedbackLevel
)

enum class SaveFeedbackLevel {
    SUCCESS,
    WARNING,
    EMPTY,
    ERROR
}

object SaveFeedbackMessageMapper {
    fun fromShareResult(result: ShareSaveResult): SaveFeedbackMessage {
        return when (result) {
            is ShareSaveResult.Success -> SaveFeedbackMessage(
                text = SUCCESS_MESSAGE,
                level = SaveFeedbackLevel.SUCCESS
            )
            is ShareSaveResult.PartialSuccess -> SaveFeedbackMessage(
                text = PARTIAL_MESSAGE,
                level = SaveFeedbackLevel.WARNING
            )
            is ShareSaveResult.Failure -> SaveFeedbackMessage(
                text = FAILURE_MESSAGE,
                level = SaveFeedbackLevel.ERROR
            )
        }
    }

    fun fromClipboardResult(result: ClipboardCaptureResult): SaveFeedbackMessage {
        return when (result) {
            is ClipboardCaptureResult.Success -> SaveFeedbackMessage(
                text = SUCCESS_MESSAGE,
                level = SaveFeedbackLevel.SUCCESS
            )
            is ClipboardCaptureResult.Failure -> when (result.reason) {
                ClipboardFailureReason.EmptyClipboard,
                ClipboardFailureReason.UnsupportedContent -> SaveFeedbackMessage(
                    text = EMPTY_CLIPBOARD_MESSAGE,
                    level = SaveFeedbackLevel.EMPTY
                )
                ClipboardFailureReason.SaveFailed -> SaveFeedbackMessage(
                    text = FAILURE_MESSAGE,
                    level = SaveFeedbackLevel.ERROR
                )
            }
        }
    }

    private const val SUCCESS_MESSAGE = "SmartClipboard에 담았어요"
    private const val PARTIAL_MESSAGE = "일부 항목을 처리하지 못했어요"
    private const val EMPTY_CLIPBOARD_MESSAGE = "복사된 내용이 없어요"
    private const val FAILURE_MESSAGE = "저장하지 못했어요"
}
