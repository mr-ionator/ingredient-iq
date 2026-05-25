package com.ingredientiq.ui.results

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ingredientiq.domain.model.IngredientMatch
import com.ingredientiq.domain.model.ScanResult
import com.ingredientiq.ui.theme.healthRatingColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    onIngredientClick: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: ResultsViewModel = hiltViewModel(),
) {
    val result by viewModel.result.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Results") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        result?.let { scan ->
            ResultsContent(
                result = scan,
                onIngredientClick = onIngredientClick,
                modifier = Modifier.padding(padding),
            )
        } ?: Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            Text("No scan results available.")
        }
    }
}

@Composable
private fun ResultsContent(
    result: ScanResult,
    onIngredientClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scoreColor = healthRatingColor(
        when {
            result.overallScore >= 70 -> 2
            result.overallScore >= 55 -> 1
            result.overallScore >= 45 -> 0
            result.overallScore >= 30 -> -1
            else -> -2
        }
    )
    val scoreLabel = when {
        result.overallScore >= 70 -> "GOOD"
        result.overallScore >= 55 -> "OKAY"
        result.overallScore >= 45 -> "NEUTRAL"
        result.overallScore >= 30 -> "CONCERNING"
        else -> "AVOID"
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Overall score card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = scoreColor.copy(alpha = 0.12f)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            "OVERALL SCORE: ${result.overallScore}/100",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = scoreColor,
                        )
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = scoreColor,
                        ) {
                            Text(
                                scoreLabel,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { result.overallScore / 100f },
                        modifier = Modifier.fillMaxWidth(),
                        color = scoreColor,
                        trackColor = scoreColor.copy(alpha = 0.2f),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${result.harmful.size} concerning · ${result.neutral.size} neutral · ${result.beneficial.size} beneficial",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        // Harmful section
        if (result.harmful.isNotEmpty()) {
            item {
                IngredientSection(
                    title = "▲ HARMFUL / CONCERNING",
                    count = result.harmful.size,
                    items = result.harmful,
                    onIngredientClick = onIngredientClick,
                    initiallyExpanded = true,
                )
            }
        }

        // Neutral section
        if (result.neutral.isNotEmpty()) {
            item {
                IngredientSection(
                    title = "✓ SAFE / NEUTRAL",
                    count = result.neutral.size,
                    items = result.neutral,
                    onIngredientClick = onIngredientClick,
                    initiallyExpanded = false,
                )
            }
        }

        // Beneficial section
        if (result.beneficial.isNotEmpty()) {
            item {
                IngredientSection(
                    title = "★ BENEFICIAL",
                    count = result.beneficial.size,
                    items = result.beneficial,
                    onIngredientClick = onIngredientClick,
                    initiallyExpanded = false,
                )
            }
        }

        // Unrecognized section
        if (result.unrecognized.isNotEmpty()) {
            item {
                UnrecognizedSection(tokens = result.unrecognized)
            }
        }
    }
}

@Composable
private fun IngredientSection(
    title: String,
    count: Int,
    items: List<IngredientMatch>,
    onIngredientClick: (Long) -> Unit,
    initiallyExpanded: Boolean,
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.animateContentSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "$title ($count)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                )
            }
            if (expanded) {
                HorizontalDivider()
                items.forEach { match ->
                    IngredientRow(match = match, onIngredientClick = onIngredientClick)
                }
            }
        }
    }
}

@Composable
private fun IngredientRow(match: IngredientMatch, onIngredientClick: (Long) -> Unit) {
    val ingredient = match.ingredient
    val ratingColor = healthRatingColor(match.rating)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = ingredient != null) {
                ingredient?.id?.let { onIngredientClick(it) }
            }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(10.dp),
            shape = MaterialTheme.shapes.extraSmall,
            color = ratingColor,
        ) {}
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    ingredient?.canonicalName ?: match.token,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
                ingredient?.eNumber?.let { eNum ->
                    Spacer(Modifier.width(6.dp))
                    Text(
                        eNum,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            ingredient?.let {
                Text(
                    it.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                )
            }
        }
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
}

@Composable
private fun UnrecognizedSection(tokens: List<String>) {
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.animateContentSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "? UNRECOGNIZED (${tokens.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                )
            }
            if (expanded) {
                HorizontalDivider()
                tokens.forEach { token ->
                    Text(
                        "• $token",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
