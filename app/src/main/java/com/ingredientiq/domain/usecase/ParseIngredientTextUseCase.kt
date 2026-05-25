package com.ingredientiq.domain.usecase

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParseIngredientTextUseCase @Inject constructor() {

    // Stop signals that indicate end of ingredients list
    private val stopPatterns = listOf(
        "NUTRITION FACTS", "NUTRITION INFORMATION", "SUPPLEMENT FACTS",
        "DIRECTIONS", "ALLERGEN INFO", "ALLERGEN INFORMATION",
        "CONTAINS:", "MANUFACTURED", "DISTRIBUTED", "PERCENT DAILY VALUE",
        "SERVING SIZE",
    )

    // Anchors that introduce the ingredients section
    private val anchorRegex = Regex(
        "(?i)(INGREDIENTS?\\s*:|CONTAINS\\s*:|MADE WITH\\s*:|INGREDIENTES\\s*:)",
    )

    // Tokens to discard
    private val discardRegex = Regex("^(and|or|and/or|\\d+[%.]?\\d*%?)$", RegexOption.IGNORE_CASE)

    operator fun invoke(rawText: String): List<String> {
        val anchor = anchorRegex.find(rawText) ?: return emptyList()
        val afterAnchor = rawText.substring(anchor.range.last + 1)

        // Truncate at stop signal
        val ingredientsText = stopSignalIndex(afterAnchor)
            .let { if (it >= 0) afterAnchor.substring(0, it) else afterAnchor }

        return tokenize(ingredientsText)
    }

    private fun stopSignalIndex(text: String): Int {
        val upper = text.uppercase()
        return stopPatterns
            .mapNotNull { upper.indexOf(it).takeIf { i -> i >= 0 } }
            .minOrNull() ?: -1
    }

    private fun tokenize(text: String): List<String> {
        // Expand parenthetical sub-ingredients: "A (B, C)" → ["A", "B", "C"]
        val expanded = expandParentheticals(text)
        return expanded
            .split(Regex("[,;\\n]"))
            .map { it.trim() }
            .map { it.replace(Regex("[*†‡§©®™%]"), "") } // strip footnote markers
            .map { it.replace(Regex("\\band/or\\b", RegexOption.IGNORE_CASE), "") }
            .map { it.trim() }
            .filter { token ->
                token.isNotBlank()
                    && !discardRegex.matches(token)
                    && token.length > 1
            }
    }

    private fun expandParentheticals(text: String): String {
        // Replace "(content)" with ", content" so they become separate tokens
        return text.replace(Regex("\\(([^)]+)\\)")) { match ->
            ", ${match.groupValues[1]}"
        }
    }
}
