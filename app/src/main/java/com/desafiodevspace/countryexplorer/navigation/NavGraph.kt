package com.desafiodevspace.countryexplorer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.desafiodevspace.countryexplorer.ui.screen.CountryDetailScreen
import com.desafiodevspace.countryexplorer.ui.screen.CountryListScreen
import com.desafiodevspace.countryexplorer.ui.screen.FavoritesScreen
import com.desafiodevspace.countryexplorer.viewmodel.CountryViewModel

@Composable
fun CountryNavGraph(navController: NavHostController, viewModel: CountryViewModel) {
    // Observa favoritos
    val favorites = viewModel.favorites.collectAsState().value

    NavHost(navController = navController, startDestination = Routes.CountryList.route) {
        composable(Routes.CountryList.route) {
            CountryListScreen(
                viewModel = viewModel,
                onCountryClick = { code ->
                    navController.navigate(Routes.CountryDetail.createRoute(code))
                }
            )
        }

        composable(Routes.Favorites.route) {
            FavoritesScreen(
                countries = viewModel.countriesUi.collectAsState().value,
                favorites = favorites,
                viewModel= viewModel,
                onCountryClick = { code ->
                    navController.navigate(Routes.CountryDetail.createRoute(code))
                }
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
                onBack = { navController.popBackStack() },
                onNavigateToCountryDetail = { borderCode ->
                    navController.navigate(Routes.CountryDetail.createRoute(borderCode)) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
