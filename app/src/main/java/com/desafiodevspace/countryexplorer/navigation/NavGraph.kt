package com.desafiodevspace.countryexplorer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.desafiodevspace.countryexplorer.ui.screen.CountryDetailScreen
import com.desafiodevspace.countryexplorer.ui.screen.CountryListScreen
import com.desafiodevspace.countryexplorer.ui.screen.FavoritesScreen
import com.desafiodevspace.countryexplorer.ui.viewmodel.CountryViewModel

@Composable
fun CountryNavGraph(navController: NavHostController, viewModel: CountryViewModel) {
    NavHost(navController = navController, startDestination = Routes.CountryList.route) {
        composable(Routes.CountryList.route) {
            CountryListScreen(
                viewModel = viewModel,
                onCountryClick = { code -> navController.navigate(Routes.CountryDetail.createRoute(code)) },
                onFavoriteClick = {}
            )
        }

        composable(Routes.Favorites.route) {
            FavoritesScreen(
                favorites = listOf(),
                onCountryClick = { code -> navController.navigate(Routes.CountryDetail.createRoute(code)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.CountryDetail.route,
            arguments = listOf(navArgument("countryCode") { type = NavType.StringType })
        ) { backStackEntry ->
            val code = backStackEntry.arguments?.getString("countryCode") ?: return@composable
            CountryDetailScreen(
                countryCode = code,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
