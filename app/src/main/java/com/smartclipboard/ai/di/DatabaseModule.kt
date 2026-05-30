package com.smartclipboard.ai.di

import android.content.Context
import androidx.room.Room
import com.smartclipboard.ai.data.source.local.SmartClipboardDatabase
import com.smartclipboard.ai.data.source.local.dao.DataItemDao
import com.smartclipboard.ai.data.source.local.dao.TopicActionDao
import com.smartclipboard.ai.data.source.local.dao.TopicAnalysisDao
import com.smartclipboard.ai.data.source.local.dao.TopicDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): SmartClipboardDatabase {
        return Room.databaseBuilder(
            context,
            SmartClipboardDatabase::class.java,
            "smart_clipboard.db"
        ).build()
    }

    @Provides
    fun provideDataItemDao(database: SmartClipboardDatabase): DataItemDao {
        return database.dataItemDao()
    }

    @Provides
    fun provideTopicDao(database: SmartClipboardDatabase): TopicDao {
        return database.topicDao()
    }

    @Provides
    fun provideTopicAnalysisDao(database: SmartClipboardDatabase): TopicAnalysisDao {
        return database.topicAnalysisDao()
    }

    @Provides
    fun provideTopicActionDao(database: SmartClipboardDatabase): TopicActionDao {
        return database.topicActionDao()
    }
}
