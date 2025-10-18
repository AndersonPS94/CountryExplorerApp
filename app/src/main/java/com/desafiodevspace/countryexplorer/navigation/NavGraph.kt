package com.desafiodevspace.countryexplorer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.desafiodevspace.countryexplorer.data.model.CountryUiModel
import com.desafiodevspace.countryexplorer.ui.screen.CountryDetailScreen
import com.desafiodevspace.countryexplorer.ui.screen.CountryListWithFilters
import com.desafiodevspace.countryexplorer.ui.screen.FavoritesScreen

@Composable
fun CountryNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.CountryList.route
    ) {
        // Lista de países
        composable(Routes.CountryList.route) {
            val mockCountries = listOf(
                CountryUiModel("Brasil", "América do Sul", "https://flagcdn.com/br.png", "BRA"),
                CountryUiModel("França", "Europa", "https://flagcdn.com/fr.png", "FRA"),
                CountryUiModel("Japão", "Ásia", "https://flagcdn.com/jp.png", "JPN"),
                CountryUiModel("Canadá", "América do Norte", "https://flagcdn.com/ca.png", "CAN")
            )
            val mockFavorites = listOf("Brasil")

            CountryListWithFilters(
                countries = mockCountries,
                favorites = mockFavorites,
                onFavoriteClick = { countryName -> /* TODO: implementar favoritos */ },
                onCountryClick = { code ->
                    navController.navigate(Routes.CountryDetail.createRoute(code))
                }
            )
        }

        // Favoritos
        composable(Routes.Favorites.route) {
            val mockFavorites = listOf("Brasil", "Japão")

            FavoritesScreen(
                favorites = mockFavorites,
                onCountryClick = { code ->
                    navController.navigate(Routes.CountryDetail.createRoute(code))
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Detalhes do país
        composable(
            route = Routes.CountryDetail.route,
            arguments = listOf(navArgument("countryCode") { type = NavType.StringType })
        ) { backStackEntry ->
            val code = backStackEntry.arguments?.getString("countryCode") ?: return@composable
            CountryDetailScreen(
                countryCode = code,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
