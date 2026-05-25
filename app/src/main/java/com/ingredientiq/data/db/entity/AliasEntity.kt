package com.ingredientiq.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "aliases",
    foreignKeys = [ForeignKey(
        entity = IngredientEntity::class,
        parentColumns = ["id"],
        childColumns = ["ingredientId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["normalizedAlias"]), Index(value = ["ingredientId"])]
)
data class AliasEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ingredientId: Long,
    val alias: String,
    val normalizedAlias: String
)
