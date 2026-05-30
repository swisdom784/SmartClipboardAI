package com.smartclipboard.ai.collection.clipboard

import android.content.ClipData
import android.content.Context

object ClipboardReader {
    fun readPrimaryText(context: Context, clipData: ClipData?): ClipboardPayload {
        val item = clipData
            ?.takeIf { it.itemCount > 0 }
            ?.getItemAt(0)
            ?: return ClipboardPayload(text = null)

        val text = item?.text?.toString()
            ?: item?.htmlText

        if (!text.isNullOrBlank()) {
            return ClipboardPayload(text = text)
        }

        val unsupportedText = runCatching {
            item.coerceToText(context)?.toString()
        }.getOrNull()
        return ClipboardPayload(
            text = null,
            hasUnsupportedContent = !unsupportedText.isNullOrBlank()
        )
    }
}
