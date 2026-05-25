package com.ingredientiq.domain.model

data class ScanResult(
    val rawText: String,
    val tokens: List<String>,
    val matches: List<IngredientMatch>,
    val overallScore: Int,
    val harmful: List<IngredientMatch>,
    val neutral: List<IngredientMatch>,
    val beneficial: List<IngredientMatch>,
    val unrecognized: List<String>,
)

data class IngredientMatch(
    val token: String,
    val ingredient: Ingredient?,
    val rating: Int,
)
