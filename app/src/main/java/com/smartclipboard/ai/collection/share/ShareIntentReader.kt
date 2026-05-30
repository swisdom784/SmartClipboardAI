package com.smartclipboard.ai.collection.share

import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns

object ShareIntentReader {
    fun fromIntent(intent: Intent, contentResolver: ContentResolver): SharePayload {
        val action = when (intent.action) {
            Intent.ACTION_SEND_MULTIPLE -> ShareAction.SendMultiple
            else -> ShareAction.Send
        }

        return SharePayload(
            action = action,
            mimeType = intent.type,
            text = intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString(),
            streams = intent.readSharedUris(contentResolver)
        )
    }

    private fun Intent.readSharedUris(contentResolver: ContentResolver): List<SharedUri> {
        val uris = when (action) {
            Intent.ACTION_SEND_MULTIPLE -> readUriListExtra()
            Intent.ACTION_SEND -> listOfNotNull(readUriExtra())
            else -> emptyList()
        }

        return uris.map { uri ->
            val metadata = contentResolver.readOpenableMetadata(uri)
            SharedUri(
                uri = uri.toString(),
                mimeType = runCatching { contentResolver.getType(uri) }.getOrNull() ?: type,
                displayName = metadata.displayName,
                sizeBytes = metadata.sizeBytes
            )
        }
    }

    @Suppress("DEPRECATION")
    private fun Intent.readUriExtra(): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            getParcelableExtra(Intent.EXTRA_STREAM) as? Uri
        }
    }

    @Suppress("DEPRECATION")
    private fun Intent.readUriListExtra(): List<Uri> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri::class.java).orEmpty()
        } else {
            getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM).orEmpty()
        }
    }

    private fun ContentResolver.readOpenableMetadata(uri: Uri): OpenableMetadata {
        return runCatching {
            query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)
                ?.use { cursor -> cursor.toOpenableMetadata() }
                ?: OpenableMetadata()
        }.getOrDefault(OpenableMetadata())
    }

    private fun Cursor.toOpenableMetadata(): OpenableMetadata {
        if (!moveToFirst()) {
            return OpenableMetadata()
        }

        return OpenableMetadata(
            displayName = stringOrNull(OpenableColumns.DISPLAY_NAME),
            sizeBytes = longOrNull(OpenableColumns.SIZE)
        )
    }

    private fun Cursor.stringOrNull(columnName: String): String? {
        val index = getColumnIndex(columnName)
        return if (index >= 0 && !isNull(index)) getString(index) else null
    }

    private fun Cursor.longOrNull(columnName: String): Long? {
        val index = getColumnIndex(columnName)
        return if (index >= 0 && !isNull(index)) getLong(index) else null
    }

    private data class OpenableMetadata(
        val displayName: String? = null,
        val sizeBytes: Long? = null
    )
}
