package com.example.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ComponentPhotoRepository private constructor(private val context: Context) {

    private val database = TavFurnaceDatabase.getDatabase(context)
    private val dao = database.componentPhotoDao()

    val allPhotos: Flow<List<ComponentPhotoEntity>> = dao.getAllPhotos()

    suspend fun getPhotoForComponent(componentId: String): ComponentPhotoEntity? {
        return withContext(Dispatchers.IO) {
            dao.getPhotoForComponent(componentId)
        }
    }

    suspend fun savePhotoFromUri(componentId: String, uri: Uri, notes: String = ""): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val photosDir = File(context.filesDir, "component_photos")
                if (!photosDir.exists()) {
                    photosDir.mkdirs()
                }

                val targetFile = File(photosDir, "cmp_${componentId}_${System.currentTimeMillis()}.jpg")

                // Open input stream from content resolver
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()

                    if (bitmap != null) {
                        // Compress and save locally to app files directory
                        val outputStream = FileOutputStream(targetFile)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 92, outputStream)
                        outputStream.flush()
                        outputStream.close()

                        // Remove any old photo file if it exists for this component
                        val existing = dao.getPhotoForComponent(componentId)
                        if (existing != null) {
                            val oldFile = File(existing.localFilePath)
                            if (oldFile.exists()) {
                                oldFile.delete()
                            }
                        }

                        // Save entry to Room database
                        dao.savePhoto(
                            ComponentPhotoEntity(
                                componentId = componentId,
                                localFilePath = targetFile.absolutePath,
                                dateAdded = System.currentTimeMillis(),
                                notes = notes
                            )
                        )
                        return@withContext true
                    }
                }
                false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun deletePhoto(componentId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val existing = dao.getPhotoForComponent(componentId)
                if (existing != null) {
                    val file = File(existing.localFilePath)
                    if (file.exists()) {
                        file.delete()
                    }
                    dao.deletePhoto(componentId)
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ComponentPhotoRepository? = null

        fun getInstance(context: Context): ComponentPhotoRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = ComponentPhotoRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
