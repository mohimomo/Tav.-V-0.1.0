package com.example.data.local

import android.content.Context
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import com.example.model.GroundingWebSource

class AiResponseCacheRepository private constructor(
    private val cachedAiResponseDao: CachedAiResponseDao
) {
    fun getAllCachedResponses(): Flow<List<CachedAiResponseEntity>> {
        return cachedAiResponseDao.getAllCachedResponses()
    }

    fun searchCachedResponses(query: String): Flow<List<CachedAiResponseEntity>> {
        return cachedAiResponseDao.searchCachedResponses(query)
    }

    suspend fun getExactCachedResponse(query: String): CachedAiResponseEntity? {
        return cachedAiResponseDao.getExactCachedResponse(query.trim())
    }

    suspend fun cacheResponse(
        query: String,
        responseText: String,
        mode: String,
        groundings: List<GroundingWebSource> = emptyList(),
        generatedImageBase64: String? = null
    ) {
        val groundingsJsonStr = if (groundings.isNotEmpty()) {
            val jsonArr = JSONArray()
            groundings.forEach { g ->
                val obj = JSONObject()
                    .put("title", g.title)
                    .put("uri", g.uri)
                jsonArr.put(obj)
            }
            jsonArr.toString()
        } else {
            ""
        }

        val entity = CachedAiResponseEntity(
            query = query.trim(),
            responseText = responseText,
            mode = mode,
            timestamp = System.currentTimeMillis(),
            groundingsJson = groundingsJsonStr,
            generatedImageBase64 = generatedImageBase64
        )
        cachedAiResponseDao.insertCachedResponse(entity)
    }

    suspend fun deleteCachedResponse(id: String) {
        cachedAiResponseDao.deleteById(id)
    }

    suspend fun clearCache() {
        cachedAiResponseDao.clearAll()
    }

    companion object {
        @Volatile
        private var INSTANCE: AiResponseCacheRepository? = null

        fun getInstance(context: Context): AiResponseCacheRepository {
            return INSTANCE ?: synchronized(this) {
                val db = TavFurnaceDatabase.getDatabase(context)
                val instance = AiResponseCacheRepository(db.cachedAiResponseDao())
                INSTANCE = instance
                instance
            }
        }
    }
}
