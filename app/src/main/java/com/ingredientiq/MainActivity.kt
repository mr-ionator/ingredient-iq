package com.ingredientiq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ingredientiq.data.seeder.DatabaseSeeder
import com.ingredientiq.ui.nav.IngredientIQNavGraph
import com.ingredientiq.ui.theme.IngredientIQTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var seeder: DatabaseSeeder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Seed DB on first launch (fast no-op if already seeded)
        CoroutineScope(Dispatchers.IO).launch { seeder.seedIfNeeded() }

        enableEdgeToEdge()
        setContent {
            IngredientIQTheme {
                IngredientIQNavGraph()
            }
        }
    }
}
