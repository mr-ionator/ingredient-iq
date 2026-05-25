package com.ingredientiq.di

import android.content.Context
import androidx.room.Room
import com.ingredientiq.data.db.AppDatabase
import com.ingredientiq.data.db.dao.AliasDao
import com.ingredientiq.data.db.dao.IngredientDao
import com.ingredientiq.data.db.dao.ScanHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "ingredient_iq.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideIngredientDao(db: AppDatabase): IngredientDao = db.ingredientDao()

    @Provides
    fun provideAliasDao(db: AppDatabase): AliasDao = db.aliasDao()

    @Provides
    fun provideScanHistoryDao(db: AppDatabase): ScanHistoryDao = db.scanHistoryDao()
}
