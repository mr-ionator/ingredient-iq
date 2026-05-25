package com.ingredientiq.data.seeder

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.ingredientiq.data.db.dao.AliasDao
import com.ingredientiq.data.db.dao.IngredientDao
import com.ingredientiq.data.db.entity.AliasEntity
import com.ingredientiq.data.db.entity.IngredientEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

sealed class SeedState {
    object Idle : SeedState()
    data class Seeding(val progress: Int, val total: Int) : SeedState()
    object Done : SeedState()
    data class Error(val message: String) : SeedState()
}

@Singleton
class DatabaseSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ingredientDao: IngredientDao,
    private val aliasDao: AliasDao,
    private val prefs: SharedPreferences,
) {
    private val _state = MutableStateFlow<SeedState>(SeedState.Idle)
    val state: StateFlow<SeedState> = _state

    companion object {
        private const val PREFS_KEY = "db_seeded_version"
        private const val CURRENT_SEED_VERSION = 1
        private const val ASSET_FILE = "ingredients_db.json"
        private const val BATCH_SIZE = 100
    }

    suspend fun seedIfNeeded() {
        if (prefs.getInt(PREFS_KEY, 0) >= CURRENT_SEED_VERSION) {
            _state.value = SeedState.Done
            return
        }
        withContext(Dispatchers.IO) {
            try {
                val seedData = loadSeedData()
                val total = seedData.ingredients.size
                _state.value = SeedState.Seeding(0, total)

                var processed = 0
                seedData.ingredients.chunked(BATCH_SIZE).forEach { batch ->
                    val entities = batch.map { it.toEntity() }
                    ingredientDao.insertAll(entities)

                    // Aliases need IDs — re-query after insert to get auto-generated IDs
                    // We use canonical name to correlate since IDs auto-generate
                    val aliases = mutableListOf<AliasEntity>()
                    batch.forEach { seedIngredient ->
                        val entity = ingredientDao.findByCanonicalName(seedIngredient.canonicalName)
                        entity?.let { ing ->
                            seedIngredient.aliases.forEach { alias ->
                                aliases.add(
                                    AliasEntity(
                                        ingredientId = ing.id,
                                        alias = alias,
                                        normalizedAlias = normalize(alias),
                                    )
                                )
                            }
                        }
                    }
                    aliasDao.insertAll(aliases)

                    processed += batch.size
                    _state.value = SeedState.Seeding(processed, total)
                }

                prefs.edit().putInt(PREFS_KEY, CURRENT_SEED_VERSION).apply()
                _state.value = SeedState.Done
            } catch (e: Exception) {
                _state.value = SeedState.Error(e.message ?: "Unknown seeding error")
            }
        }
    }

    private fun loadSeedData(): SeedData {
        val json = context.assets.open(ASSET_FILE).bufferedReader().use { it.readText() }
        return Gson().fromJson(json, SeedData::class.java)
    }

    fun normalize(text: String): String =
        text.lowercase().replace(Regex("[^a-z0-9\\-\\s]"), "").trim()
}

// ── Seed JSON model ────────────────────────────────────────────────────────────

data class SeedData(
    val version: Int,
    val ingredients: List<SeedIngredient>,
)

data class SeedIngredient(
    val canonicalName: String,
    val eNumber: String?,
    val category: String,
    val healthRating: Int,
    val summary: String,
    val fullDescription: String,
    val regulatoryStatus: String,
    val evidenceLevel: String,
    val commonUses: String,   // already a JSON array string in the asset
    val allergenType: String?,
    val safeLimit: String?,
    val concerns: String,     // already a JSON array string in the asset
    val benefits: String,     // already a JSON array string in the asset
    val aliases: List<String>,
) {
    fun toEntity(): IngredientEntity = IngredientEntity(
        canonicalName = canonicalName,
        eNumber = eNumber,
        category = category,
        healthRating = healthRating,
        summary = summary,
        fullDescription = fullDescription,
        regulatoryStatus = regulatoryStatus,
        evidenceLevel = evidenceLevel,
        commonUses = commonUses,
        allergenType = allergenType,
        safeLimit = safeLimit,
        concerns = concerns,
        benefits = benefits,
    )
}
