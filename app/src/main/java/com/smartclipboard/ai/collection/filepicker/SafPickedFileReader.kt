package com.smartclipboard.ai.collection.filepicker

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns

object SafPickedFileReader {
    fun read(contentResolver: ContentResolver, uris: List<Uri>): List<SafPickedFile> {
        return uris.map { uri ->
            val metadata = contentResolver.readOpenableMetadata(uri)
            SafPickedFile(
                uri = uri.toString(),
                mimeType = runCatching { contentResolver.getType(uri) }.getOrNull(),
                displayName = metadata.displayName,
                sizeBytes = metadata.sizeBytes
            )
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
