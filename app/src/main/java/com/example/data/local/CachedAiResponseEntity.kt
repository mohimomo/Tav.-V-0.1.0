package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cached_ai_responses",
    indices = [
        Index(value = ["query"]),
        Index(value = ["timestamp"]),
        Index(value = ["mode"])
    ]
)
data class CachedAiResponseEntity(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val query: String,
    val responseText: String,
    val mode: String,
    val timestamp: Long = System.currentTimeMillis(),
    val groundingsJson: String = "",
    val generatedImageBase64: String? = null
)
