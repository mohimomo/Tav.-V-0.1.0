package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ManualDao {
    @Query("SELECT * FROM manual_documents ORDER BY pageNumber ASC")
    fun getAllDocuments(): Flow<List<ManualDocumentEntity>>

    @Query("SELECT * FROM manual_documents WHERE id = :id LIMIT 1")
    fun getDocumentById(id: String): Flow<ManualDocumentEntity?>

    @Query("SELECT * FROM manual_documents WHERE pageNumber = :pageNumber LIMIT 1")
    fun getDocumentByPage(pageNumber: Int): Flow<ManualDocumentEntity?>

    @Query("SELECT * FROM manual_documents WHERE chapterNumber = :chapterNumber ORDER BY pageNumber ASC")
    fun getDocumentsByChapter(chapterNumber: String): Flow<List<ManualDocumentEntity>>

    @Query("SELECT * FROM manual_documents WHERE category = :category ORDER BY pageNumber ASC")
    fun getDocumentsByCategory(category: String): Flow<List<ManualDocumentEntity>>

    @Query("""
        SELECT * FROM manual_documents 
        WHERE sectionTitleEn LIKE '%' || :query || '%'
           OR sectionTitleAr LIKE '%' || :query || '%'
           OR contentEn LIKE '%' || :query || '%'
           OR contentAr LIKE '%' || :query || '%'
           OR rawPdfOcrExcerpt LIKE '%' || :query || '%'
           OR technicianNotes LIKE '%' || :query || '%'
           OR chapterTitleEn LIKE '%' || :query || '%'
           OR chapterTitleAr LIKE '%' || :query || '%'
        ORDER BY pageNumber ASC
    """)
    fun searchManual(query: String): Flow<List<ManualDocumentEntity>>

    @Query("SELECT * FROM manual_documents WHERE isBookmarked = 1 ORDER BY pageNumber ASC")
    fun getBookmarkedDocuments(): Flow<List<ManualDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(documents: List<ManualDocumentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(document: ManualDocumentEntity)

    @Update
    suspend fun update(document: ManualDocumentEntity)

    @Query("UPDATE manual_documents SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: String, isBookmarked: Boolean)

    @Query("UPDATE manual_documents SET technicianNotes = :note, lastUpdated = :timestamp WHERE id = :id")
    suspend fun updateTechnicianNote(id: String, note: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM manual_documents")
    suspend fun getCount(): Int

    @Query("DELETE FROM manual_documents")
    suspend fun deleteAll()
}
