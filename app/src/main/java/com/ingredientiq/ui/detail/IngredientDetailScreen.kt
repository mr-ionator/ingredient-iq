package com.ingredientiq.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ingredientiq.domain.model.Ingredient
import com.ingredientiq.ui.theme.healthRatingColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientDetailScreen(
    onBack: () -> Unit,
    viewModel: IngredientDetailViewModel = hiltViewModel(),
) {
    val ingredient by viewModel.ingredient.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ingredient?.canonicalName ?: "Ingredient") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        ingredient?.let { ing ->
            IngredientContent(ing, Modifier.padding(padding))
        } ?: Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun IngredientContent(ing: Ingredient, modifier: Modifier = Modifier) {
    val ratingColor = healthRatingColor(ing.healthRating)
    val ratingLabel = when (ing.healthRating) {
        -2 -> "HARMFUL"
        -1 -> "CONCERNING"
        0  -> "NEUTRAL"
        1  -> "BENEFICIAL"
        else -> "VERY BENEFICIAL"
    }
    val evidenceLabel = ing.evidenceLevel.replaceFirstChar { it.uppercase() }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header: name + E-number + rating badge
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        ing.canonicalName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                }
                ing.eNumber?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(12.dp))
                // Large rating badge
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = ratingColor,
                ) {
                    Text(
                        ratingLabel,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        // Category + evidence chips
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip(
                    onClick = {},
                    label = { Text(ing.category.replace('_', ' ')) },
                )
                SuggestionChip(
                    onClick = {},
                    label = { Text("Evidence: $evidenceLabel") },
                )
                ing.allergenType?.let { allergen ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text("Allergen: ${allergen.replace('_', ' ')}") },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                    )
                }
            }
        }

        // Regulatory status
        item {
            DetailCard(title = "Regulatory Status") {
                Text(ing.regulatoryStatus, style = MaterialTheme.typography.bodyMedium)
            }
        }

        // Summary
        item {
            Text(ing.summary, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }

        // Full description
        item {
            Text(ing.fullDescription, style = MaterialTheme.typography.bodyMedium)
        }

        // Safe limit
        ing.safeLimit?.let { limit ->
            item {
                DetailCard(title = "Safe Limit") {
                    Text(limit, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Common uses
        if (ing.commonUses.isNotEmpty()) {
            item {
                DetailCard(title = "Commonly Found In") {
                    Text(
                        ing.commonUses.joinToString(" · "),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        // Concerns
        if (ing.concerns.isNotEmpty()) {
            item {
                DetailCard(title = "Concerns") {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        ing.concerns.forEach { concern ->
                            Row {
                                Text("• ", color = MaterialTheme.colorScheme.error)
                                Text(
                                    concern,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    }
                }
            }
        }

        // Benefits
        if (ing.benefits.isNotEmpty()) {
            item {
                DetailCard(title = "Benefits") {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        ing.benefits.forEach { benefit ->
                            Row {
                                Text("• ", color = Color(0xFF388E3C))
                                Text(
                                    benefit,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun DetailCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}
