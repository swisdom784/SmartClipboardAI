package com.smartclipboard.ai.collection.media

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidMediaStoreDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : MediaImageDataSource {
    override suspend fun queryImagesAddedBetween(
        fromMillisExclusive: Long,
        toMillisInclusive: Long
    ): List<MediaImageCandidate> {
        val projection = buildProjection()
        val fromSecondsExclusive = fromMillisExclusive / MILLIS_PER_SECOND
        val toSecondsInclusive = toMillisInclusive / MILLIS_PER_SECOND
        val selection = "${MediaStore.Images.Media.DATE_ADDED} > ? AND ${MediaStore.Images.Media.DATE_ADDED} <= ?"
        val selectionArgs = arrayOf(
            fromSecondsExclusive.toString(),
            toSecondsInclusive.toString()
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} ASC"

        val cursor = try {
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )
        } catch (_: SecurityException) {
            throw MediaReadPermissionMissingException()
        }

        return cursor?.use { it.toCandidates() }.orEmpty()
    }

    private fun buildProjection(): Array<String> {
        val baseProjection = mutableListOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATE_TAKEN
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            baseProjection += MediaStore.Images.Media.RELATIVE_PATH
        }

        return baseProjection.toTypedArray()
    }

    private fun Cursor.toCandidates(): List<MediaImageCandidate> {
        val candidates = mutableListOf<MediaImageCandidate>()
        while (moveToNext()) {
            val id = longOrNull(MediaStore.Images.Media._ID) ?: continue
            val dateAddedSeconds = longOrNull(MediaStore.Images.Media.DATE_ADDED) ?: continue
            val contentUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )

            candidates += MediaImageCandidate(
                mediaStoreId = id,
                contentUri = contentUri.toString(),
                displayName = stringOrNull(MediaStore.Images.Media.DISPLAY_NAME),
                mimeType = stringOrNull(MediaStore.Images.Media.MIME_TYPE),
                sizeBytes = longOrNull(MediaStore.Images.Media.SIZE),
                addedAtMillis = dateAddedSeconds * MILLIS_PER_SECOND,
                takenAtMillis = longOrNull(MediaStore.Images.Media.DATE_TAKEN)
                    ?.takeIf { it > 0L },
                relativePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    stringOrNull(MediaStore.Images.Media.RELATIVE_PATH)
                } else {
                    null
                }
            )
        }
        return candidates
    }

    private fun Cursor.stringOrNull(columnName: String): String? {
        val index = getColumnIndex(columnName)
        return if (index >= 0 && !isNull(index)) getString(index) else null
    }

    private fun Cursor.longOrNull(columnName: String): Long? {
        val index = getColumnIndex(columnName)
        return if (index >= 0 && !isNull(index)) getLong(index) else null
    }

    private companion object {
        const val MILLIS_PER_SECOND = 1000L
    }
}
