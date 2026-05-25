package com.ingredientiq.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val canonicalName: String,
    val eNumber: String?,
    val category: String,
    val healthRating: Int,
    val summary: String,
    val fullDescription: String,
    val regulatoryStatus: String,
    val evidenceLevel: String,
    val commonUses: String,
    val allergenType: String?,
    val safeLimit: String?,
    val concerns: String,
    val benefits: String
)
