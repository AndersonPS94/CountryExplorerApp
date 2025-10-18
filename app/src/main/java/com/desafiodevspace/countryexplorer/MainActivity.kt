package com.desafiodevspace.countryexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.desafiodevspace.countryexplorer.navigation.CountryNavGraph
import com.desafiodevspace.countryexplorer.navigation.Routes
import com.desafiodevspace.countryexplorer.ui.theme.CountryExplorerTheme
import com.desafiodevspace.countryexplorer.ui.viewmodel.CountryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CountryExplorerTheme {
                val navController = rememberNavController()
                val countryViewModel: CountryViewModel = viewModel()

                var selectedItem by remember { mutableStateOf(Routes.CountryList.route) }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = selectedItem == Routes.CountryList.route,
                                onClick = {
                                    selectedItem = Routes.CountryList.route
                                    navController.navigate(Routes.CountryList.route) {
                                        popUpTo(Routes.CountryList.route) { inclusive = true }
                                    }
                                },
                                icon = { Icon(Icons.Default.Public, contentDescription = "Países") },
                                label = { Text("Países") }
                            )
                            NavigationBarItem(
                                selected = selectedItem == Routes.Favorites.route,
                                onClick = {
                                    selectedItem = Routes.Favorites.route
                                    navController.navigate(Routes.Favorites.route) {
                                        popUpTo(Routes.CountryList.route)
                                    }
                                },
                                icon = { Icon(Icons.Default.Star, contentDescription = "Favoritos") },
                                label = { Text("Favoritos") }
                            )
                        }
                    }
                ) { paddingValues ->
                    Column(modifier = Modifier.padding(paddingValues)) {
                        CountryNavGraph(navController, countryViewModel)
                    }
                }
            }
        }
    }
}
