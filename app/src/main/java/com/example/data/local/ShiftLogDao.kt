package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ShiftLogDao {
    @Query("SELECT * FROM shift_logs ORDER BY timestamp DESC")
    fun getAllShiftLogs(): Flow<List<ShiftLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: ShiftLogEntity): Long

    @Delete
    suspend fun delete(log: ShiftLogEntity)

    @Query("DELETE FROM shift_logs")
    suspend fun clearAll()
}
