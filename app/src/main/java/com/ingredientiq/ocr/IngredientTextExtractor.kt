package com.ingredientiq.ocr

import com.ingredientiq.domain.usecase.ParseIngredientTextUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IngredientTextExtractor @Inject constructor(
    private val parseText: ParseIngredientTextUseCase,
) {
    fun hasIngredientsSection(rawText: String): Boolean =
        rawText.contains(Regex("(?i)ingredients?\\s*:"))

    fun extract(rawText: String): List<String> = parseText(rawText)
}
