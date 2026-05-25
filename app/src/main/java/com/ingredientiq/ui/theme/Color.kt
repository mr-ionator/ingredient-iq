package com.ingredientiq.ui.theme

import androidx.compose.ui.graphics.Color

// Health rating colors (from spec)
val HealthRatingNeg2 = Color(0xFFD32F2F)  // Deep Red: harmful/avoid
val HealthRatingNeg1 = Color(0xFFF57C00)  // Orange: concerning/limit
val HealthRatingZero = Color(0xFF757575)  // Grey: neutral
val HealthRatingPos1 = Color(0xFF388E3C)  // Light Green: mildly beneficial
val HealthRatingPos2 = Color(0xFF1B5E20)  // Deep Green: clearly beneficial

// Material theme colors
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650A4)
val PurpleGrey40 = Color(0xFF625B71)
val Pink40 = Color(0xFF7D5260)

fun healthRatingColor(rating: Int): Color = when {
    rating <= -2 -> HealthRatingNeg2
    rating == -1 -> HealthRatingNeg1
    rating == 0  -> HealthRatingZero
    rating == 1  -> HealthRatingPos1
    else         -> HealthRatingPos2
}
