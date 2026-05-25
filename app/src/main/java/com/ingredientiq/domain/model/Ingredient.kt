package com.ingredientiq.domain.model

data class Ingredient(
    val id: Long,
    val canonicalName: String,
    val eNumber: String?,
    val category: String,
    val healthRating: Int,
    val summary: String,
    val fullDescription: String,
    val regulatoryStatus: String,
    val evidenceLevel: String,
    val commonUses: List<String>,
    val allergenType: String?,
    val safeLimit: String?,
    val concerns: List<String>,
    val benefits: List<String>,
)
