package com.example.data.local

import android.content.Context
import kotlinx.coroutines.flow.Flow

class ShiftLogRepository private constructor(private val dao: ShiftLogDao) {
    val allShiftLogs: Flow<List<ShiftLogEntity>> = dao.getAllShiftLogs()

    suspend fun insertShiftLog(log: ShiftLogEntity): Long = dao.insert(log)
    suspend fun deleteShiftLog(log: ShiftLogEntity) = dao.delete(log)
    suspend fun clearAll() = dao.clearAll()

    companion object {
        @Volatile
        private var instance: ShiftLogRepository? = null

        fun getInstance(context: Context): ShiftLogRepository {
            return instance ?: synchronized(this) {
                val db = TavFurnaceDatabase.getDatabase(context)
                val newInstance = ShiftLogRepository(db.shiftLogDao())
                instance = newInstance
                newInstance
            }
        }
    }
}
