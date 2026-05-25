package com.ingredientiq.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ingredientiq.data.db.entity.IngredientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ingredients: List<IngredientEntity>)

    @Query("SELECT * FROM ingredients WHERE id = :id")
    suspend fun getById(id: Long): IngredientEntity?

    @Query("SELECT COUNT(*) FROM ingredients")
    fun count(): Flow<Int>

    @Query("SELECT * FROM ingredients WHERE canonicalName LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<IngredientEntity>
}
