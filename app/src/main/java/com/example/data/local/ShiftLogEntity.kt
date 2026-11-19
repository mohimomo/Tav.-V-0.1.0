package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shift_logs")
data class ShiftLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val batchNumber: String,
    val operatorName: String,
    val processType: String, // "Hardening", "Annealing", "Brazing", "Sintering", "Tempering"
    val loadWeightKg: Double,
    val targetTempC: Double,
    val quenchPressureBar: Double,
    val status: String, // "Completed", "In Progress", "Aborted", "Holding"
    val leakRateQ: Double, // Q mbar*l/s
    val notes: String,
    val timestamp: Long = System.currentTimeMillis(),
    // Checklists (comma-separated keys of completed tasks)
    val dailyChecklist: String = "",
    val weeklyChecklist: String = "",
    val monthlyChecklist: String = "",
    // Monitoring photos
    val gasPressurePhotoUri: String? = null,
    val oilLevelPhotoUri: String? = null,
    val gasPressureReading: Double = 6.0,
    val oilLevelStatus: String = "NORMAL" // "NORMAL", "LOW", "REFILL_NEEDED"
)
