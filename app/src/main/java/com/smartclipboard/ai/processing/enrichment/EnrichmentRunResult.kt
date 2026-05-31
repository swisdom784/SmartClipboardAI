package com.smartclipboard.ai.processing.enrichment

data class EnrichmentRunResult(
    val processedCount: Int,
    val successCount: Int,
    val retryCount: Int,
    val failedCount: Int,
    val skippedCount: Int
)
