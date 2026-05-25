package com.ingredientiq.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ingredientiq.data.db.dao.AliasDao
import com.ingredientiq.data.db.dao.IngredientDao
import com.ingredientiq.data.db.dao.ScanHistoryDao
import com.ingredientiq.data.db.entity.AliasEntity
import com.ingredientiq.data.db.entity.ScanHistoryEntity
import com.ingredientiq.domain.model.Ingredient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IngredientRepository @Inject constructor(
    private val ingredientDao: IngredientDao,
    private val aliasDao: AliasDao,
    private val scanHistoryDao: ScanHistoryDao,
) {
    private val gson = Gson()
    private val listType = object : TypeToken<List<String>>() {}.type

    suspend fun getById(id: Long): Ingredient? =
        ingredientDao.getById(id)?.toDomain()

    suspend fun findByCanonicalName(name: String): Ingredient? =
        ingredientDao.findByCanonicalName(name)?.toDomain()

    suspend fun findAliasByNormalized(normalized: String): AliasEntity? =
        aliasDao.findByNormalizedAlias(normalized)

    suspend fun findAliasesByPrefix(prefix: String): List<AliasEntity> =
        aliasDao.findByPrefix(prefix)

    suspend fun getAllAliases(): List<AliasEntity> =
        aliasDao.getAllAliases()

    fun ingredientCount(): Flow<Int> = ingredientDao.count()

    suspend fun searchIngredients(query: String): List<Ingredient> =
        ingredientDao.searchWithAliases(query).map { it.toDomain() }

    suspend fun saveScan(entity: ScanHistoryEntity) =
        scanHistoryDao.insert(entity)

    fun getAllScans() = scanHistoryDao.getAll()

    suspend fun deleteScan(entity: ScanHistoryEntity) =
        scanHistoryDao.delete(entity)

    private fun com.ingredientiq.data.db.entity.IngredientEntity.toDomain(): Ingredient =
        Ingredient(
            id = id,
            canonicalName = canonicalName,
            eNumber = eNumber,
            category = category,
            healthRating = healthRating,
            summary = summary,
            fullDescription = fullDescription,
            regulatoryStatus = regulatoryStatus,
            evidenceLevel = evidenceLevel,
            commonUses = gson.fromJson(commonUses, listType),
            allergenType = allergenType,
            safeLimit = safeLimit,
            concerns = gson.fromJson(concerns, listType),
            benefits = gson.fromJson(benefits, listType),
        )
}
