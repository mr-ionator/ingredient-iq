package com.ingredientiq.di

import android.content.Context
import android.content.SharedPreferences
import com.ingredientiq.data.db.dao.AliasDao
import com.ingredientiq.data.db.dao.IngredientDao
import com.ingredientiq.data.seeder.DatabaseSeeder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideDatabaseSeeder(
        @ApplicationContext context: Context,
        ingredientDao: IngredientDao,
        aliasDao: AliasDao,
        prefs: SharedPreferences,
    ): DatabaseSeeder = DatabaseSeeder(context, ingredientDao, aliasDao, prefs)
}
