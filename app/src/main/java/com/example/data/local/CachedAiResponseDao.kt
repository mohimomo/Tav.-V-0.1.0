package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedAiResponseDao {

    @Query("SELECT * FROM cached_ai_responses ORDER BY timestamp DESC LIMIT 50")
    fun getAllCachedResponses(): Flow<List<CachedAiResponseEntity>>

    @Query("SELECT * FROM cached_ai_responses WHERE query LIKE '%' || :searchQuery || '%' OR responseText LIKE '%' || :searchQuery || '%' ORDER BY timestamp DESC")
    fun searchCachedResponses(searchQuery: String): Flow<List<CachedAiResponseEntity>>

    @Query("SELECT * FROM cached_ai_responses WHERE query = :query LIMIT 1")
    suspend fun getExactCachedResponse(query: String): CachedAiResponseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedResponse(entity: CachedAiResponseEntity)

    @Query("DELETE FROM cached_ai_responses WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM cached_ai_responses")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM cached_ai_responses")
    suspend fun getCount(): Int
}
