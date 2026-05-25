package com.ingredientiq.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ingredientiq.ui.detail.IngredientDetailScreen
import com.ingredientiq.ui.history.HistoryScreen
import com.ingredientiq.ui.home.HomeScreen
import com.ingredientiq.ui.onboarding.OnboardingScreen
import com.ingredientiq.ui.results.ResultsScreen
import com.ingredientiq.ui.scan.ScanScreen
import com.ingredientiq.ui.search.SearchScreen
import com.ingredientiq.ui.splash.SplashScreen

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val SCAN = "scan"
    const val RESULTS = "results"
    const val DETAIL = "detail/{ingredientId}"
    const val HISTORY = "history"
    const val SEARCH = "search"
    fun detail(id: Long) = "detail/$id"
}

@Composable
fun IngredientIQNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onReady = { onboardingDone ->
                    val dest = if (onboardingDone) Routes.HOME else Routes.ONBOARDING
                    navController.navigate(dest) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onScanClick = { navController.navigate(Routes.SCAN) },
                onHistoryClick = { navController.navigate(Routes.HISTORY) },
                onSearchClick = { navController.navigate(Routes.SEARCH) },
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

        composable(Routes.HISTORY) {
            HistoryScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.SEARCH) {
            SearchScreen(
                onIngredientClick = { id -> navController.navigate(Routes.detail(id)) },
                onBack = { navController.popBackStack() },
            )
        }
    }
}
