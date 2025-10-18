package com.desafiodevspace.countryexplorer.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.desafiodevspace.countryexplorer.data.model.CountryUiModel
import com.desafiodevspace.countryexplorer.ui.components.CountryCard
import com.desafiodevspace.countryexplorer.ui.components.FilterBottomSheet
import com.desafiodevspace.countryexplorer.ui.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryListWithFilters(
    countries: List<CountryUiModel>,
    favorites: List<String>,
    onFavoriteClick: (String) -> Unit,
    onCountryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* TODO: Navegar para lista de países */ },
                    icon = { Icon(Icons.Default.Public, contentDescription = "Países") },
                    label = { Text("Países") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* TODO: Navegar para favoritos */ },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Favoritos") },
                    label = { Text("Favoritos") }
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Países") },
                actions = {
                    IconButton(onClick = { showFilters = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtros")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                modifier = Modifier.fillMaxWidth()
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                val filteredCountries = countries.filter { it.name.contains(query, ignoreCase = true) }
                items(filteredCountries) { country ->
                    CountryCard(
                        flagUrl = country.flag,
                        name = country.name,
                        region = country.region,
                        isFavorite = favorites.contains(country.name),
                        onFavoriteClick = { onFavoriteClick(country.name) },
                        onClick = { onCountryClick(country.code) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }

    if (showFilters) {
        ModalBottomSheet(
            onDismissRequest = { showFilters = false }
        ) {
            var selectedRegion by remember { mutableStateOf<String?>(null) }
            var selectedPopulation by remember { mutableStateOf<String?>(null) }

            FilterBottomSheet(
                selectedRegion = selectedRegion,
                selectedPopulation = selectedPopulation,
                onSelectRegion = { selectedRegion = it },
                onSelectPopulation = { selectedPopulation = it },
                onApply = { showFilters = false },
                onClear = {
                    selectedRegion = null
                    selectedPopulation = null
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCountryListWithFilters() {
    val mockCountries = listOf(
        CountryUiModel("Brasil", "América do Sul", "https://flagcdn.com/br.png", "BRA"),
        CountryUiModel("França", "Europa", "https://flagcdn.com/fr.png", "FRA"),
        CountryUiModel("Japão", "Ásia", "https://flagcdn.com/jp.png", "JPN"),
        CountryUiModel("Canadá", "América do Norte", "https://flagcdn.com/ca.png", "CAN"),
        CountryUiModel("Austrália", "Oceania", "https://flagcdn.com/au.png", "AUS"),
        CountryUiModel("Itália", "Europa", "https://flagcdn.com/it.png", "ITA"),
        CountryUiModel("Alemanha", "Europa", "https://flagcdn.com/de.png", "DEU"),
        CountryUiModel("China", "Ásia", "https://flagcdn.com/cn.png", "CHN")
    )

    CountryListWithFilters(
        countries = mockCountries,
        favorites = listOf("Brasil"),
        onFavoriteClick = {},
        onCountryClick = {}
    )
}
