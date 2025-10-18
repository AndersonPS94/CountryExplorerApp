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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.desafiodevspace.countryexplorer.navigation.CountryNavGraph
import com.desafiodevspace.countryexplorer.navigation.Routes
import com.desafiodevspace.countryexplorer.ui.theme.CountryExplorerTheme
import com.desafiodevspace.countryexplorer.viewmodel.CountryViewModel

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
                                label = { Text("Countries") }
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
                                label = { Text("Favorites") }
                            )
                        }
                    }
                ) { paddingValues ->
                    Column(modifier = Modifier.padding(paddingValues)) {
                        CountryNavGraph(navController, countryViewModel)
                    }

                    val lifecycleOwner = LocalLifecycleOwner.current
                    DisposableEffect(lifecycleOwner) {
                        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
                            when (event) {
                                Lifecycle.Event.ON_START -> countryViewModel.refreshCountries()
                                else -> {}
                            }
                        }
                        lifecycleOwner.lifecycle.addObserver(observer)
                        onDispose {
                            lifecycleOwner.lifecycle.removeObserver(observer)
                        }
                    }
                }
            }
        }
    }
}
