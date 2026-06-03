package com.smartclipboard.ai.processing.gemini.recommendation

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiTopicRecommendationManager @Inject constructor(
    private val dataSource: RecommendationDataSource,
    private val generator: TopicRecommendationGenerator,
    private val sessionStore: RecommendationSessionStore
) {
    internal constructor(
        dataSource: RecommendationDataSource,
        generator: TopicRecommendationGenerator,
        sessionStore: RecommendationSessionStore,
        nowMillis: () -> Long
    ) : this(dataSource, generator, sessionStore) {
        this.nowMillis = nowMillis
    }

    private var nowMillis: () -> Long = { System.currentTimeMillis() }

    suspend fun refresh(limit: Int = DEFAULT_LIMIT): RecommendationSession {
        val now = nowMillis()
        val items = dataSource.getRecommendationInputItems(limit)
        val session = when {
            items.isEmpty() -> skippedSession(
                now = now,
                message = "추천할 새 자료가 없어요"
            )
            else -> runCatching { generator.generate(items) }
                .fold(
                    onSuccess = { recommendations ->
                        if (recommendations.isEmpty()) {
                            skippedSession(
                                now = now,
                                message = "추천할 흐름을 찾지 못했어요"
                            )
                        } else {
                            RecommendationSession(
                                id = "session_$now",
                                status = RecommendationSessionStatus.READY,
                                recommendations = recommendations,
                                createdAtMillis = now
                            )
                        }
                    },
                    onFailure = { throwable ->
                        failureSession(
                            now = now,
                            throwable = throwable
                        )
                    }
                )
        }

        sessionStore.replaceCurrentSession(session)
        return session
    }

    private fun failureSession(
        now: Long,
        throwable: Throwable
    ): RecommendationSession {
        val failure = throwable.toGeminiFailureOrNull()
        return when (failure) {
            GeminiFailure.MissingApiKey -> skippedSession(
                now = now,
                message = "Gemini API key가 설정되지 않았어요"
            )

            GeminiFailure.InvalidApiKey -> failedSession(
                now = now,
                message = "Gemini API key를 확인해 주세요"
            )

            GeminiFailure.NetworkFailure -> failedSession(
                now = now,
                message = "네트워크 연결을 확인해 주세요"
            )

            is GeminiFailure.ApiFailure -> failedSession(
                now = now,
                message = failure.message
            )

            is GeminiFailure.ParseFailure -> failedSession(
                now = now,
                message = failure.message
            )

            null -> failedSession(
                now = now,
                message = "추천을 준비하지 못했어요"
            )
        }
    }

    private fun failedSession(
        now: Long,
        message: String
    ): RecommendationSession {
        return RecommendationSession(
            id = "session_$now",
            status = RecommendationSessionStatus.FAILED,
            recommendations = emptyList(),
            createdAtMillis = now,
            message = message
        )
    }

    private fun skippedSession(
        now: Long,
        message: String
    ): RecommendationSession {
        return RecommendationSession(
            id = "session_$now",
            status = RecommendationSessionStatus.SKIPPED,
            recommendations = emptyList(),
            createdAtMillis = now,
            message = message
        )
    }

    private companion object {
        const val DEFAULT_LIMIT = 20
    }
}
