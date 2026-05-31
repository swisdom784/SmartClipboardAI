package com.smartclipboard.ai.processing.enrichment

import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

interface DataItemEnrichmentTrigger {
    suspend fun runAfterDataInput(savedCount: Int)
}

object NoOpDataItemEnrichmentTrigger : DataItemEnrichmentTrigger {
    override suspend fun runAfterDataInput(savedCount: Int) = Unit
}

@Singleton
class PendingDataItemEnrichmentTrigger @Inject constructor(
    private val manager: DataItemEnrichmentManager
) : DataItemEnrichmentTrigger {
    override suspend fun runAfterDataInput(savedCount: Int) {
        if (savedCount <= 0) {
            return
        }

        withTimeoutOrNull(AFTER_INPUT_TIMEOUT_MILLIS) {
            manager.runPending(limit = savedCount.coerceIn(1, MAX_ITEMS_AFTER_INPUT))
        }
    }

    private companion object {
        const val MAX_ITEMS_AFTER_INPUT = 3
        const val AFTER_INPUT_TIMEOUT_MILLIS = 2_000L
    }
}
