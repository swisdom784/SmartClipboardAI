package com.smartclipboard.ai.collection.filepicker

data class SafPickedFile(
    val uri: String,
    val mimeType: String? = null,
    val displayName: String? = null,
    val sizeBytes: Long? = null
)

sealed interface SafImportResult {
    data class Imported(
        val importedCount: Int,
        val skippedCount: Int,
        val failedCount: Int
    ) : SafImportResult

    data object EmptySelection : SafImportResult
}
