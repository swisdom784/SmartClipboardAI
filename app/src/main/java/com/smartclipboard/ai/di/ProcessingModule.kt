package com.smartclipboard.ai.di

import com.smartclipboard.ai.processing.enrichment.DataItemEnrichmentStore
import com.smartclipboard.ai.processing.enrichment.DataItemEnrichmentTrigger
import com.smartclipboard.ai.processing.enrichment.PendingDataItemEnrichmentTrigger
import com.smartclipboard.ai.processing.enrichment.RoomDataItemEnrichmentStore
import com.smartclipboard.ai.processing.gemini.recommendation.BuildConfigGeminiApiKeyProvider
import com.smartclipboard.ai.processing.gemini.recommendation.GeminiApiKeyProvider
import com.smartclipboard.ai.processing.gemini.recommendation.GeminiTextClient
import com.smartclipboard.ai.processing.gemini.recommendation.GeminiTopicRecommendationGenerator
import com.smartclipboard.ai.processing.gemini.recommendation.HttpGeminiTextClient
import com.smartclipboard.ai.processing.gemini.recommendation.InMemoryRecommendationSessionStore
import com.smartclipboard.ai.processing.gemini.recommendation.RecommendationDataSource
import com.smartclipboard.ai.processing.gemini.recommendation.RecommendationSessionStore
import com.smartclipboard.ai.processing.gemini.recommendation.RepositoryRecommendationDataSource
import com.smartclipboard.ai.processing.gemini.recommendation.TopicRecommendationGenerator
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

    @Binds
    abstract fun bindGeminiApiKeyProvider(
        provider: BuildConfigGeminiApiKeyProvider
    ): GeminiApiKeyProvider

    @Binds
    @Singleton
    abstract fun bindGeminiTextClient(
        client: HttpGeminiTextClient
    ): GeminiTextClient

    @Binds
    @Singleton
    abstract fun bindTopicRecommendationGenerator(
        generator: GeminiTopicRecommendationGenerator
    ): TopicRecommendationGenerator

    @Binds
    @Singleton
    abstract fun bindRecommendationDataSource(
        dataSource: RepositoryRecommendationDataSource
    ): RecommendationDataSource

    @Binds
    @Singleton
    abstract fun bindRecommendationSessionStore(
        store: InMemoryRecommendationSessionStore
    ): RecommendationSessionStore
}
