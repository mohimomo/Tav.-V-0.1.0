package com.example.data.local

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Local Repository providing offline access to the TAV Furnace Manual PDF content.
 * Built on Room database with pre-populated manual chapters, specifications,
 * maintenance guides, safety protocols, and technical diagrams text.
 */
class ManualRepository(
    private val manualDao: ManualDao,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    init {
        // Automatically ensure initial manual data is seeded on startup
        externalScope.launch {
            ensureSeeded()
        }
    }

    suspend fun ensureSeeded() {
        if (manualDao.getCount() == 0) {
            manualDao.insertAll(ManualContentSeeder.getInitialManualDocuments())
        }
    }

    fun getAllDocuments(): Flow<List<ManualDocumentEntity>> =
        manualDao.getAllDocuments()

    fun getDocumentById(id: String): Flow<ManualDocumentEntity?> =
        manualDao.getDocumentById(id)

    fun getDocumentByPage(pageNumber: Int): Flow<ManualDocumentEntity?> =
        manualDao.getDocumentByPage(pageNumber)

    fun getDocumentsByChapter(chapterNumber: String): Flow<List<ManualDocumentEntity>> =
        manualDao.getDocumentsByChapter(chapterNumber)

    fun getDocumentsByCategory(category: String): Flow<List<ManualDocumentEntity>> =
        manualDao.getDocumentsByCategory(category)

    fun searchManual(query: String): Flow<List<ManualDocumentEntity>> =
        manualDao.searchManual(query.trim())

    fun getBookmarkedDocuments(): Flow<List<ManualDocumentEntity>> =
        manualDao.getBookmarkedDocuments()

    suspend fun toggleBookmark(id: String, isBookmarked: Boolean) {
        manualDao.updateBookmark(id, isBookmarked)
    }

    suspend fun updateTechnicianNote(id: String, note: String) {
        manualDao.updateTechnicianNote(id, note)
    }

    suspend fun resetToDefaultContent() {
        manualDao.deleteAll()
        manualDao.insertAll(ManualContentSeeder.getInitialManualDocuments())
    }

    suspend fun insertDocument(document: ManualDocumentEntity) {
        manualDao.insert(document)
    }

    companion object {
        @Volatile
        private var INSTANCE: ManualRepository? = null

        fun getInstance(context: Context): ManualRepository {
            return INSTANCE ?: synchronized(this) {
                val database = TavFurnaceDatabase.getDatabase(context)
                val instance = ManualRepository(database.manualDao())
                INSTANCE = instance
                instance
            }
        }
    }
}
