package com.ingredientiq.domain.usecase

import com.google.gson.Gson
import com.ingredientiq.data.db.entity.ScanHistoryEntity
import com.ingredientiq.data.repository.IngredientRepository
import com.ingredientiq.domain.model.IngredientMatch
import com.ingredientiq.domain.model.ScanResult
import com.ingredientiq.matching.FuzzyMatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LookupIngredientsUseCase @Inject constructor(
    private val repository: IngredientRepository,
    private val fuzzyMatcher: FuzzyMatcher,
    private val parseText: ParseIngredientTextUseCase,
) {
    private val gson = Gson()

    suspend operator fun invoke(rawText: String, productName: String? = null): ScanResult {
        val tokens = parseText(rawText)
        val matches = tokens.map { token ->
            val ingredient = fuzzyMatcher.match(token)
            IngredientMatch(
                token = token,
                ingredient = ingredient,
                rating = ingredient?.healthRating ?: 0,
            )
        }

        val recognized = matches.filter { it.ingredient != null }
        val overallScore = if (recognized.isEmpty()) 50
        else {
            val avg = recognized.map { it.rating }.average()
            // Scale from [-2..+2] to [0..100]
            ((avg + 2) / 4.0 * 100).toInt().coerceIn(0, 100)
        }

        val result = ScanResult(
            rawText = rawText,
            tokens = tokens,
            matches = matches,
            overallScore = overallScore,
            harmful = matches.filter { it.rating <= -1 },
            neutral = matches.filter { it.rating == 0 },
            beneficial = matches.filter { it.rating >= 1 },
            unrecognized = matches.filter { it.ingredient == null }.map { it.token },
        )

        // Persist to scan history
        repository.saveScan(
            ScanHistoryEntity(
                timestamp = System.currentTimeMillis(),
                productName = productName,
                rawOcrText = rawText,
                foundIngredients = gson.toJson(recognized.mapNotNull { it.ingredient?.id }),
                overallScore = overallScore,
            )
        )

        return result
    }
}
