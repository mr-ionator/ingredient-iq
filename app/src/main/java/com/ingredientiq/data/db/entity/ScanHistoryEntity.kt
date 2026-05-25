package com.ingredientiq.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val productName: String?,
    val rawOcrText: String,
    val foundIngredients: String,
    val overallScore: Int
)
