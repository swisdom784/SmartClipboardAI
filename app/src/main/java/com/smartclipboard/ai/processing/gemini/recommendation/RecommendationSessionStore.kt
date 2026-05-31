package com.smartclipboard.ai.processing.gemini.recommendation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface RecommendationSessionStore {
    val currentSession: StateFlow<RecommendationSession?>

    suspend fun replaceCurrentSession(session: RecommendationSession)

    suspend fun clear()
}

@Singleton
class InMemoryRecommendationSessionStore @Inject constructor() : RecommendationSessionStore {
    private val mutableCurrentSession = MutableStateFlow<RecommendationSession?>(null)

    override val currentSession: StateFlow<RecommendationSession?> =
        mutableCurrentSession.asStateFlow()

    override suspend fun replaceCurrentSession(session: RecommendationSession) {
        mutableCurrentSession.value = session
    }

    override suspend fun clear() {
        mutableCurrentSession.value = null
    }
}
