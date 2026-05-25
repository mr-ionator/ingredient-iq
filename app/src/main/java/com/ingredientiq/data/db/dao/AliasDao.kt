package com.ingredientiq.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ingredientiq.data.db.entity.AliasEntity

@Dao
interface AliasDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(aliases: List<AliasEntity>)

    @Query("SELECT * FROM aliases WHERE normalizedAlias = :normalized LIMIT 1")
    suspend fun findByNormalizedAlias(normalized: String): AliasEntity?

    @Query("SELECT * FROM aliases WHERE normalizedAlias LIKE :prefix || '%' LIMIT 2")
    suspend fun findByPrefix(prefix: String): List<AliasEntity>

    @Query("SELECT * FROM aliases")
    suspend fun getAllAliases(): List<AliasEntity>
}
