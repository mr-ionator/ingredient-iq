package com.ingredientiq.matching

import com.ingredientiq.data.repository.IngredientRepository
import com.ingredientiq.domain.model.Ingredient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FuzzyMatcher @Inject constructor(
    private val repository: IngredientRepository,
) {
    // In-memory alias cache: normalizedAlias → ingredientId
    @Volatile private var aliasCache: HashMap<String, Long>? = null

    suspend fun match(rawToken: String): Ingredient? {
        val normalized = normalize(rawToken)
        if (normalized.isBlank()) return null

        // Step 1: exact alias lookup (indexed query)
        repository.findAliasByNormalized(normalized)?.let { alias ->
            return repository.getById(alias.ingredientId)
        }

        // Step 2: prefix match — only if single unambiguous result
        val prefixMatches = repository.findAliasesByPrefix(normalized)
        if (prefixMatches.size == 1) {
            return repository.getById(prefixMatches[0].ingredientId)
        }

        // Step 3: fuzzy Levenshtein over in-memory cache
        val cache = getOrBuildCache()
        val threshold = minOf(2, (normalized.length * 0.2).toInt())
        var bestId: Long? = null
        var bestDist = Int.MAX_VALUE
        for ((alias, id) in cache) {
            val dist = levenshtein(normalized, alias)
            if (dist <= threshold && dist < bestDist) {
                bestDist = dist
                bestId = id
            }
        }
        return bestId?.let { repository.getById(it) }
    }

    fun normalize(text: String): String =
        text.lowercase().replace(Regex("[^a-z0-9\\-\\s]"), "").trim()

    private suspend fun getOrBuildCache(): HashMap<String, Long> {
        aliasCache?.let { return it }
        val map = HashMap<String, Long>()
        repository.getAllAliases().forEach { map[it.normalizedAlias] = it.ingredientId }
        aliasCache = map
        return map
    }

    fun invalidateCache() { aliasCache = null }

    private fun levenshtein(a: String, b: String): Int {
        if (a == b) return 0
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length
        val dp = Array(a.length + 1) { IntArray(b.length + 1) { 0 } }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j
        for (i in 1..a.length) {
            for (j in 1..b.length) {
                dp[i][j] = if (a[i - 1] == b[j - 1]) dp[i - 1][j - 1]
                else 1 + minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1])
            }
        }
        return dp[a.length][b.length]
    }
}
