package com.smartclipboard.ai.di

import com.smartclipboard.ai.processing.enrichment.DataItemEnrichmentStore
import com.smartclipboard.ai.processing.enrichment.DataItemEnrichmentTrigger
import com.smartclipboard.ai.processing.enrichment.PendingDataItemEnrichmentTrigger
import com.smartclipboard.ai.processing.enrichment.RoomDataItemEnrichmentStore
import com.smartclipboard.ai.processing.ocr.MlKitOcrProcessor
import com.smartclipboard.ai.processing.ocr.OcrProcessor
import com.smartclipboard.ai.processing.web.JsoupWebExtractor
import com.smartclipboard.ai.processing.web.WebExtractor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProcessingModule {
    @Binds
    @Singleton
    abstract fun bindDataItemEnrichmentStore(
        store: RoomDataItemEnrichmentStore
    ): DataItemEnrichmentStore

    @Binds
    @Singleton
    abstract fun bindDataItemEnrichmentTrigger(
        trigger: PendingDataItemEnrichmentTrigger
    ): DataItemEnrichmentTrigger

    @Binds
    @Singleton
    abstract fun bindWebExtractor(
        extractor: JsoupWebExtractor
    ): WebExtractor

    @Binds
    @Singleton
    abstract fun bindOcrProcessor(
        processor: MlKitOcrProcessor
    ): OcrProcessor
}
