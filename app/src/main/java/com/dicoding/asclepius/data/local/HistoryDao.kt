package com.dicoding.asclepius.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.asclepius.data.local.entity.HistoryEntity

@Dao
interface HistoryDao{

    @Query("SELECT * FROM history WHERE image = :imageUri LIMIT 1")
    suspend fun checkHistoryExists(imageUri: String): HistoryEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHistory(history: List<HistoryEntity>)

    @Delete
    suspend fun deleteHistory(history: HistoryEntity)

    @Query("SELECT * FROM history")
    fun getHistory(): LiveData<List<HistoryEntity>>

}