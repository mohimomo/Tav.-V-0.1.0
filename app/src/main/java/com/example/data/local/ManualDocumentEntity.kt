package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "manual_documents",
    indices = [
        Index(value = ["chapterNumber"]),
        Index(value = ["pageNumber"]),
        Index(value = ["category"])
    ]
)
data class ManualDocumentEntity(
    @PrimaryKey
    val id: String, // e.g. "doc_ch1_p5", "doc_ch4_p14"
    val pageNumber: Int,
    val documentType: String = "OPERATION_AND_MAINTENANCE_MANUAL",
    val chapterNumber: String,
    val chapterTitleEn: String,
    val chapterTitleAr: String,
    val sectionNumber: String,
    val sectionTitleEn: String,
    val sectionTitleAr: String,
    val contentEn: String,
    val contentAr: String,
    val keyPointsEn: String,
    val keyPointsAr: String,
    val category: String, // GENERAL, SAFETY, DESCRIPTION, SOFTWARE, OPERATION, ALARMS, MAINTENANCE, CE_PLATE
    val rawPdfOcrExcerpt: String,
    val isBookmarked: Boolean = false,
    val technicianNotes: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)
