package com.desafiodevspace.countryexplorer.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.desafiodevspace.countryexplorer.ui.components.CountryCard
import com.desafiodevspace.countryexplorer.ui.components.FilterBottomSheet
import com.desafiodevspace.countryexplorer.ui.components.SearchBar
import com.desafiodevspace.countryexplorer.viewmodel.CountryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryListScreen(
    viewModel: CountryViewModel,
    onCountryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredCountries by viewModel.filteredCountries.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val query by viewModel.searchQuery.collectAsState()
    val selectedRegion by viewModel.selectedRegion.collectAsState()
    val selectedPopulation by viewModel.selectedPopulation.collectAsState()

    var isSheetVisible by remember { mutableStateOf(false) }

    // Filter bottom sheet
    if (isSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { isSheetVisible = false },
            content = {
                FilterBottomSheet(
                    selectedRegion = selectedRegion,
                    selectedPopulation = selectedPopulation,
                    onSelectRegion = { viewModel.updateRegionFilter(it) },
                    onSelectPopulation = { viewModel.updatePopulationFilter(it) },
                    onApply = { isSheetVisible = false },
                    onClear = {
                        viewModel.updateRegionFilter(null)
                        viewModel.updatePopulationFilter(null)
                        isSheetVisible = false
                    }
                )
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Countries")
                    }
                },
                actions = {
                    IconButton(onClick = { isSheetVisible = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
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
            // Search bar
            SearchBar(
                query = query,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth()
            )

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                !errorMessage.isNullOrEmpty() -> {
                    Text(
                        text = errorMessage ?: "Unknown error",
                        modifier = Modifier.padding(16.dp)
                    )
                }

                filteredCountries.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No countries found for the current search and filters.",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp)
                    ) {
                        items(filteredCountries) { country ->
                            CountryCard(
                                flagUrl = country.flag,
                                name = country.name,
                                region = country.region,
                                isFavorite = favorites.contains(country.code),
                                onFavoriteClick = { viewModel.toggleFavorite(country.code) },
                                onClick = { onCountryClick(country.code) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
