package com.ingredientiq.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ingredientiq.data.db.dao.AliasDao
import com.ingredientiq.data.db.dao.IngredientDao
import com.ingredientiq.data.db.dao.ScanHistoryDao
import com.ingredientiq.data.db.entity.AliasEntity
import com.ingredientiq.data.db.entity.IngredientEntity
import com.ingredientiq.data.db.entity.ScanHistoryEntity

@Database(
    entities = [IngredientEntity::class, AliasEntity::class, ScanHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientDao
    abstract fun aliasDao(): AliasDao
    abstract fun scanHistoryDao(): ScanHistoryDao
}
