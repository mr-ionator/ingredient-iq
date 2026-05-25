package com.ingredientiq.matching

import com.ingredientiq.domain.model.Ingredient
import com.ingredientiq.domain.model.IngredientMatch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IngredientMatcher @Inject constructor(
    private val fuzzyMatcher: FuzzyMatcher,
) {
    // Match a list of tokens, returning one IngredientMatch per token.
    // Parenthetical sub-ingredients are already expanded by ParseIngredientTextUseCase
    // so each token here is a single ingredient candidate.
    suspend fun matchAll(tokens: List<String>): List<IngredientMatch> =
        tokens.map { token ->
            val ingredient = fuzzyMatcher.match(token)
            IngredientMatch(
                token = token,
                ingredient = ingredient,
                rating = ingredient?.healthRating ?: 0,
            )
        }

    suspend fun matchSingle(token: String): Ingredient? =
        fuzzyMatcher.match(token)

    fun normalize(token: String): String = fuzzyMatcher.normalize(token)
}
