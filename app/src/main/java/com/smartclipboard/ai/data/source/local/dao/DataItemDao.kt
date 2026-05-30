package com.smartclipboard.ai.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.smartclipboard.ai.data.source.local.entity.DataItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DataItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DataItemEntity): Long

    @Update
    suspend fun update(entity: DataItemEntity)

    @Query("SELECT * FROM data_items WHERE id = :id")
    suspend fun getById(id: Long): DataItemEntity?

    @Query(
        """
        SELECT * FROM data_items
        WHERE deletedAtMillis IS NULL
        ORDER BY capturedAtMillis DESC, id DESC
        """
    )
    fun observeAll(): Flow<List<DataItemEntity>>

    @Query(
        """
        SELECT * FROM data_items
        WHERE deletedAtMillis IS NULL AND type IN (:types)
        ORDER BY capturedAtMillis DESC, id DESC
        """
    )
    fun observeByTypes(types: List<String>): Flow<List<DataItemEntity>>
}
