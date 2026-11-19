package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for furnace component specifications and schema paths.
 */
@Dao
interface ComponentSpecDao {

    @Query("SELECT * FROM component_specs ORDER BY componentId ASC")
    fun getAllComponentSpecs(): Flow<List<ComponentSpecEntity>>

    @Query("SELECT * FROM component_specs WHERE componentId = :componentId")
    fun getComponentSpecById(componentId: String): Flow<ComponentSpecEntity?>

    @Query("SELECT * FROM component_specs WHERE componentId = :componentId")
    suspend fun findComponentSpecById(componentId: String): ComponentSpecEntity?

    @Query("SELECT * FROM component_specs WHERE tag = :tag OR pidCode = :tag LIMIT 1")
    suspend fun findComponentSpecByTag(tag: String): ComponentSpecEntity?

    @Query("SELECT * FROM component_specs WHERE subsystem = :subsystem ORDER BY tag ASC")
    fun getComponentSpecsBySubsystem(subsystem: String): Flow<List<ComponentSpecEntity>>

    @Query("""
        SELECT * FROM component_specs 
        WHERE tag LIKE '%' || :query || '%' 
           OR pidCode LIKE '%' || :query || '%' 
           OR nameEn LIKE '%' || :query || '%' 
           OR nameAr LIKE '%' || :query || '%' 
           OR drawingCode LIKE '%' || :query || '%' 
           OR plcAddressTag LIKE '%' || :query || '%' 
           OR terminalBlock LIKE '%' || :query || '%' 
           OR specifications LIKE '%' || :query || '%' 
           OR schemaFilePath LIKE '%' || :query || '%'
        ORDER BY tag ASC
    """)
    fun searchComponentSpecs(query: String): Flow<List<ComponentSpecEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComponentSpec(spec: ComponentSpecEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(specs: List<ComponentSpecEntity>)

    @Update
    suspend fun updateComponentSpec(spec: ComponentSpecEntity)

    @Query("UPDATE component_specs SET schemaFilePath = :schemaPath, updatedAt = :timestamp WHERE componentId = :componentId")
    suspend fun updateSchemaPath(componentId: String, schemaPath: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE component_specs SET specifications = :specifications, updatedAt = :timestamp WHERE componentId = :componentId")
    suspend fun updateSpecifications(componentId: String, specifications: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE component_specs SET customNotes = :notes, updatedAt = :timestamp WHERE componentId = :componentId")
    suspend fun updateCustomNotes(componentId: String, notes: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM component_specs WHERE componentId = :componentId")
    suspend fun deleteComponentSpecById(componentId: String)

    @Query("DELETE FROM component_specs")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM component_specs")
    suspend fun getCount(): Int
}
