package com.ingredientiq.ui.splash

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ingredientiq.data.seeder.SeedState

@Composable
fun SplashScreen(
    onReady: (onboardingDone: Boolean) -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val state by viewModel.seedState.collectAsState()

    LaunchedEffect(state) {
        if (state is SeedState.Done) onReady(viewModel.isOnboardingDone())
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                "IngredientIQ",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                "Know what's in your food.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(32.dp))
            when (val s = state) {
                is SeedState.Seeding -> {
                    LinearProgressIndicator(
                        progress = { s.progress.toFloat() / s.total.coerceAtLeast(1) },
                        modifier = Modifier.fillMaxWidth(0.6f),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Loading ingredient database…",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                is SeedState.Error -> Text(
                    s.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
                else -> CircularProgressIndicator()
            }
        }
    }
}
