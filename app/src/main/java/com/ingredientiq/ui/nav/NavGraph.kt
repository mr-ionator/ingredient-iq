package com.ingredientiq.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ingredientiq.ui.detail.IngredientDetailScreen
import com.ingredientiq.ui.home.HomeScreen
import com.ingredientiq.ui.results.ResultsScreen
import com.ingredientiq.ui.scan.ScanScreen

object Routes {
    const val HOME = "home"
    const val SCAN = "scan"
    const val RESULTS = "results"
    const val DETAIL = "detail/{ingredientId}"
    fun detail(id: Long) = "detail/$id"
}

@Composable
fun IngredientIQNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onScanClick = { navController.navigate(Routes.SCAN) },
                onAboutClick = { /* Phase 2 */ },
            )
        }

        composable(Routes.SCAN) {
            ScanScreen(
                onAnalysisDone = {
                    navController.navigate(Routes.RESULTS) {
                        popUpTo(Routes.SCAN) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.RESULTS) {
            ResultsScreen(
                onIngredientClick = { id -> navController.navigate(Routes.detail(id)) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("ingredientId") { type = NavType.LongType }),
        ) {
            IngredientDetailScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
