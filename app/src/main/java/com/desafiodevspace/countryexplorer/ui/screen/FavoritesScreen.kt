package com.desafiodevspace.countryexplorer.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.desafiodevspace.countryexplorer.data.model.CountryUiModel
import com.desafiodevspace.countryexplorer.ui.components.CountryCard
import com.desafiodevspace.countryexplorer.viewmodel.CountryViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FavoritesScreen(
    viewModel: CountryViewModel = viewModel(),
    countries: List<CountryUiModel>,
    favorites: Set<String>,
    onCountryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val favoriteCountries = countries.filter { favorites.contains(it.code) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Favorites")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (favoriteCountries.isEmpty()) {
                Text(
                    text = "No favorites found.",
                    modifier = Modifier
                        .padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(favoriteCountries, key = { it.code }) { country ->
                        var visible by remember { mutableStateOf(true) }

                        AnimatedVisibility(
                            visible = visible,
                            exit = fadeOut() + slideOutVertically()
                        ) {
                            CountryCard(
                                flagUrl = country.flag,
                                name = country.name,
                                region = country.region,
                                isFavorite = true,
                                onFavoriteClick = {
                                    viewModel.toggleFavorite(country.code)
                                    visible = false
                                },
                                onClick = { onCountryClick(country.code) },
                                modifier = Modifier.animateItemPlacement()
                            )
                        }
                    }
                }
            }
        }
    }
}