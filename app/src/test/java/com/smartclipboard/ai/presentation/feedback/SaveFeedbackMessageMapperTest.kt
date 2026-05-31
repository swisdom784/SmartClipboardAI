package com.smartclipboard.ai.presentation.feedback

import com.smartclipboard.ai.collection.clipboard.ClipboardCaptureResult
import com.smartclipboard.ai.collection.clipboard.ClipboardFailureReason
import com.smartclipboard.ai.collection.share.ShareFailureReason
import com.smartclipboard.ai.collection.share.ShareSaveResult
import org.junit.Assert.assertEquals
import org.junit.Test

class SaveFeedbackMessageMapperTest {
    @Test
    fun mapsSuccessfulSaveToShortPositiveMessage() {
        val message = SaveFeedbackMessageMapper.fromShareResult(
            ShareSaveResult.Success(savedCount = 1)
        )

        assertEquals("SmartClipboard에 담았어요", message.text)
        assertEquals(SaveFeedbackLevel.SUCCESS, message.level)
    }

    @Test
    fun mapsPartialShareSaveToCalmWarningMessage() {
        val message = SaveFeedbackMessageMapper.fromShareResult(
            ShareSaveResult.PartialSuccess(savedCount = 1, skippedCount = 2)
        )

        assertEquals("일부 항목을 처리하지 못했어요", message.text)
        assertEquals(SaveFeedbackLevel.WARNING, message.level)
    }

    @Test
    fun mapsEmptyClipboardToPlainEmptyMessage() {
        val message = SaveFeedbackMessageMapper.fromClipboardResult(
            ClipboardCaptureResult.Failure(ClipboardFailureReason.EmptyClipboard)
        )

        assertEquals("복사된 내용이 없어요", message.text)
        assertEquals(SaveFeedbackLevel.EMPTY, message.level)
    }

    @Test
    fun mapsSaveFailureToShortFailureMessage() {
        val shareMessage = SaveFeedbackMessageMapper.fromShareResult(
            ShareSaveResult.Failure(ShareFailureReason.SaveFailed)
        )
        val clipboardMessage = SaveFeedbackMessageMapper.fromClipboardResult(
            ClipboardCaptureResult.Failure(ClipboardFailureReason.SaveFailed)
        )

        assertEquals("저장하지 못했어요", shareMessage.text)
        assertEquals(SaveFeedbackLevel.ERROR, shareMessage.level)
        assertEquals("저장하지 못했어요", clipboardMessage.text)
        assertEquals(SaveFeedbackLevel.ERROR, clipboardMessage.level)
    }
}
