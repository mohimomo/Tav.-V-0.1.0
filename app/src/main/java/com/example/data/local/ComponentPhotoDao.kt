package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ComponentPhotoDao {
    @Query("SELECT * FROM component_photos")
    fun getAllPhotos(): Flow<List<ComponentPhotoEntity>>

    @Query("SELECT * FROM component_photos WHERE componentId = :componentId")
    suspend fun getPhotoForComponent(componentId: String): ComponentPhotoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePhoto(photo: ComponentPhotoEntity)

    @Query("DELETE FROM component_photos WHERE componentId = :componentId")
    suspend fun deletePhoto(componentId: String)
}
