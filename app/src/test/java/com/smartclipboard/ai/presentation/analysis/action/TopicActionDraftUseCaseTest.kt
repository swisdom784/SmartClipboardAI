package com.smartclipboard.ai.presentation.analysis.action

import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.Topic
import com.smartclipboard.ai.domain.model.TopicAction
import com.smartclipboard.ai.domain.model.TopicActionStatus
import com.smartclipboard.ai.domain.model.TopicActionTargetApp
import com.smartclipboard.ai.domain.model.TopicActionType
import com.smartclipboard.ai.domain.model.TopicAnalysis
import com.smartclipboard.ai.domain.model.TopicAnalysisStatus
import com.smartclipboard.ai.domain.model.TopicItemSelectedBy
import com.smartclipboard.ai.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class TopicActionDraftUseCaseTest {
    @Test
    fun createsNotesCalendarReminderDraftsFromDoneAnalysis() = runBlocking {
        val repository = RecordingActionRepository(
            analyses = listOf(doneAnalysis())
        )
        val useCase = TopicActionDraftUseCase(
            repository = repository,
            nowMillis = { 1_000L }
        )

        val result = useCase.ensureDrafts(topicId = 7L)

        assertEquals(TopicActionDraftResult.Created(count = 3), result)
        assertEquals(
            listOf(TopicActionType.NOTE, TopicActionType.CALENDAR, TopicActionType.REMINDER),
            repository.actions.value.map { it.type }
        )
        assertEquals(
            listOf(
                TopicActionTargetApp.SAMSUNG_NOTES,
                TopicActionTargetApp.SAMSUNG_CALENDAR,
                TopicActionTargetApp.SAMSUNG_REMINDER
            ),
            repository.actions.value.map { it.targetApp }
        )
        assertEquals(listOf(4L, 4L, 4L), repository.actions.value.map { it.analysisId })
        assertEquals(TopicActionStatus.PENDING_REVIEW, repository.actions.value.first().status)
    }

    @Test
    fun doesNotDuplicateExistingDrafts() = runBlocking {
        val existing = action(id = 99L, type = TopicActionType.NOTE)
        val repository = RecordingActionRepository(
            analyses = listOf(doneAnalysis()),
            actions = listOf(existing)
        )
        val useCase = TopicActionDraftUseCase(
            repository = repository,
            nowMillis = { 1_000L }
        )

        val result = useCase.ensureDrafts(topicId = 7L)

        assertEquals(TopicActionDraftResult.Created(count = 2), result)
        assertEquals(
            listOf(TopicActionType.NOTE, TopicActionType.CALENDAR, TopicActionType.REMINDER),
            repository.actions.value.map { it.type }
        )
    }

    @Test
    fun completesSingleAction() = runBlocking {
        val repository = RecordingActionRepository(
            actions = listOf(action(id = 11L, type = TopicActionType.REMINDER))
        )
        val useCase = TopicActionDraftUseCase(
            repository = repository,
            nowMillis = { 2_000L }
        )

        useCase.completeAction(repository.actions.value.single())

        val updated = repository.actions.value.single()
        assertEquals(TopicActionStatus.COMPLETED, updated.status)
        assertEquals(2_000L, updated.completedAtMillis)
    }

    @Test
    fun marksExportedActionAsCompletedForExternalAppHandoff() = runBlocking {
        val repository = RecordingActionRepository(
            actions = listOf(action(id = 13L, type = TopicActionType.NOTE))
        )
        val useCase = TopicActionDraftUseCase(
            repository = repository,
            nowMillis = { 4_000L }
        )

        useCase.markActionExported(repository.actions.value.single())

        val updated = repository.actions.value.single()
        assertEquals(TopicActionStatus.EXPORTED, updated.status)
        assertEquals(4_000L, updated.updatedAtMillis)
        assertEquals(4_000L, updated.completedAtMillis)
    }

    @Test
    fun updatesActionContentAndPreview() = runBlocking {
        val repository = RecordingActionRepository(
            actions = listOf(action(id = 12L, type = TopicActionType.NOTE))
        )
        val useCase = TopicActionDraftUseCase(
            repository = repository,
            nowMillis = { 3_000L }
        )

        useCase.updateActionContent(
            action = repository.actions.value.single(),
            title = "수정한 노트",
            body = "사용자가 직접 다듬은 긴 본문입니다."
        )

        val updated = repository.actions.value.single()
        assertEquals("수정한 노트", updated.title)
        assertEquals("사용자가 직접 다듬은 긴 본문입니다.", updated.body)
        assertEquals("사용자가 직접 다듬은 긴 본문입니다.", updated.previewText)
        assertEquals(3_000L, updated.updatedAtMillis)
    }

    private class RecordingActionRepository(
        analyses: List<TopicAnalysis> = emptyList(),
        actions: List<TopicAction> = emptyList()
    ) : DataRepository {
        private val analyses = MutableStateFlow(analyses)
        val actions = MutableStateFlow(actions)

        override suspend fun saveDataItem(item: DataItem): Long = error("not used")
        override suspend fun getDataItem(id: Long): DataItem? = null
        override fun observeDataItems(): Flow<List<DataItem>> = emptyFlow()
        override fun observeDataItemsByType(types: Set<DataItemType>): Flow<List<DataItem>> = emptyFlow()
        override suspend fun createTopic(
            topic: Topic,
            dataItemIds: List<Long>,
            selectedBy: TopicItemSelectedBy
        ): Long = error("not used")
        override fun observeTopics(): Flow<List<Topic>> = emptyFlow()
        override fun observeDataItemsForTopic(topicId: Long): Flow<List<DataItem>> = emptyFlow()
        override suspend fun saveTopicAnalysis(analysis: TopicAnalysis): Long = error("not used")
        override fun observeTopicAnalyses(topicId: Long): Flow<List<TopicAnalysis>> = analyses
        override suspend fun saveTopicAction(action: TopicAction): Long {
            val id = (actions.value.maxOfOrNull { it.id } ?: 0L) + 1L
            actions.value = actions.value + action.copy(id = id)
            return id
        }
        override suspend fun updateTopicAction(action: TopicAction) {
            actions.value = actions.value.map { existing ->
                if (existing.id == action.id) action else existing
            }
        }
        override fun observeTopicActions(topicId: Long): Flow<List<TopicAction>> = actions
    }

    private fun doneAnalysis(): TopicAnalysis {
        return TopicAnalysis(
            id = 4L,
            topicId = 7L,
            status = TopicAnalysisStatus.DONE,
            summary = "출장 준비 자료를 정리했습니다.",
            evidence = listOf("dataItemId=1: 항공권", "dataItemId=2: 숙소"),
            createdAtMillis = 100L,
            updatedAtMillis = 100L
        )
    }

    private fun action(
        id: Long,
        type: TopicActionType
    ): TopicAction {
        return TopicAction(
            id = id,
            topicId = 7L,
            analysisId = 4L,
            type = type,
            targetApp = TopicActionTargetApp.NONE,
            title = "초안",
            body = "본문",
            previewText = "본문",
            createdAtMillis = 100L,
            updatedAtMillis = 100L
        )
    }
}
