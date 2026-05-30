package com.smartclipboard.ai.data.repository

import com.smartclipboard.ai.data.source.local.dao.DataItemDao
import com.smartclipboard.ai.data.source.local.dao.TopicActionDao
import com.smartclipboard.ai.data.source.local.dao.TopicAnalysisDao
import com.smartclipboard.ai.data.source.local.dao.TopicDao
import com.smartclipboard.ai.data.source.local.entity.TopicItemCrossRefEntity
import com.smartclipboard.ai.data.source.local.mapper.toDomain
import com.smartclipboard.ai.data.source.local.mapper.toEntity
import com.smartclipboard.ai.domain.model.DataItem
import com.smartclipboard.ai.domain.model.DataItemType
import com.smartclipboard.ai.domain.model.Topic
import com.smartclipboard.ai.domain.model.TopicAction
import com.smartclipboard.ai.domain.model.TopicAnalysis
import com.smartclipboard.ai.domain.model.TopicItemSelectedBy
import com.smartclipboard.ai.domain.repository.DataRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataRepositoryImpl @Inject constructor(
    private val dataItemDao: DataItemDao,
    private val topicDao: TopicDao,
    private val topicAnalysisDao: TopicAnalysisDao,
    private val topicActionDao: TopicActionDao
) : DataRepository {
    override suspend fun saveDataItem(item: DataItem): Long {
        return dataItemDao.insert(item.toEntity())
    }

    override suspend fun getDataItem(id: Long): DataItem? {
        return dataItemDao.getById(id)?.toDomain()
    }

    override fun observeDataItems(): Flow<List<DataItem>> {
        return dataItemDao.observeAll().map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeDataItemsByType(types: Set<DataItemType>): Flow<List<DataItem>> {
        val typeNames = types.map { it.name }
        return dataItemDao.observeByTypes(typeNames).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun createTopic(
        topic: Topic,
        dataItemIds: List<Long>,
        selectedBy: TopicItemSelectedBy
    ): Long {
        val topicId = topicDao.insertTopic(topic.toEntity())
        if (dataItemIds.isNotEmpty()) {
            topicDao.insertTopicItemCrossRefs(
                dataItemIds.map { dataItemId ->
                    TopicItemCrossRefEntity(
                        topicId = topicId,
                        dataItemId = dataItemId,
                        selectedBy = selectedBy.name,
                        createdAtMillis = topic.createdAtMillis
                    )
                }
            )
        }
        return topicId
    }

    override fun observeTopics(): Flow<List<Topic>> {
        return topicDao.observeTopics().map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeDataItemsForTopic(topicId: Long): Flow<List<DataItem>> {
        return topicDao.observeDataItemsForTopic(topicId)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun saveTopicAnalysis(analysis: TopicAnalysis): Long {
        return topicAnalysisDao.insert(analysis.toEntity())
    }

    override fun observeTopicAnalyses(topicId: Long): Flow<List<TopicAnalysis>> {
        return topicAnalysisDao.observeByTopicId(topicId)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun saveTopicAction(action: TopicAction): Long {
        return topicActionDao.insert(action.toEntity())
    }

    override suspend fun updateTopicAction(action: TopicAction) {
        topicActionDao.update(action.toEntity())
    }

    override fun observeTopicActions(topicId: Long): Flow<List<TopicAction>> {
        return topicActionDao.observeByTopicId(topicId)
            .map { entities -> entities.map { it.toDomain() } }
    }
}
