package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "component_photos")
data class ComponentPhotoEntity(
    @PrimaryKey
    val componentId: String,
    val localFilePath: String,
    val dateAdded: Long = System.currentTimeMillis(),
    val notes: String = ""
)
