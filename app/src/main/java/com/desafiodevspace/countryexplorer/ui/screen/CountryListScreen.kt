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
import com.desafiodevspace.countryexplorer.data.model.CountryUiModel
import com.desafiodevspace.countryexplorer.ui.components.CountryCard
import com.desafiodevspace.countryexplorer.ui.components.FilterBottomSheet
import com.desafiodevspace.countryexplorer.ui.components.SearchBar
import com.desafiodevspace.countryexplorer.ui.viewmodel.CountryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryListScreen(
    viewModel: CountryViewModel,
    onCountryClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val countries by viewModel.countries.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var query by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf<String?>(null) }
    var selectedPopulation by remember { mutableStateOf<String?>(null) }
    var isSheetVisible by remember { mutableStateOf(false) }

    // Estado da lista filtrada
    var filteredCountries by remember { mutableStateOf(listOf<CountryUiModel>()) }

    // Converte Country -> CountryUiModel
    val countriesUi = countries.mapNotNull { country ->
        try {
            CountryUiModel(
                name = country.name.common,
                region = country.region,
                flag = country.flags.png,
                code = country.cca3,
                population = country.population
            )
        } catch (e: Exception) {
            null
        }
    }

    // Função auxiliar: populações
    fun populationMatches(population: Long, range: String?): Boolean {
        return when (range) {
            "<1M" -> population < 1_000_000
            "1M-10M" -> population in 1_000_000..10_000_000
            "10M-100M" -> population in 10_000_000..100_000_000
            ">100M" -> population > 100_000_000
            else -> true
        }
    }

    // Função que aplica filtros
    fun applyFilters() {
        filteredCountries = countriesUi
            .filter { it.name.contains(query, ignoreCase = true) }
            .filter { selectedRegion == null || it.region == selectedRegion }
            .filter { selectedPopulation == null || populationMatches(it.population, selectedPopulation) }
    }

    // Atualiza lista assim que países são carregados
    LaunchedEffect(countriesUi, query, selectedRegion, selectedPopulation) {
        applyFilters()
    }

    // BottomSheet de filtros
    if (isSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { isSheetVisible = false },
            content = {
                FilterBottomSheet(
                    selectedRegion = selectedRegion,
                    selectedPopulation = selectedPopulation,
                    onSelectRegion = { selectedRegion = it },
                    onSelectPopulation = { selectedPopulation = it },
                    onApply = {
                        applyFilters()
                        isSheetVisible = false
                    },
                    onClear = {
                        selectedRegion = null
                        selectedPopulation = null
                        applyFilters()
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
                        Text(text = "Países")
                    }
                },
                actions = {
                    IconButton(onClick = { isSheetVisible = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtrar")
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
            SearchBar(
                query = query,
                onQueryChange = {
                    query = it
                    applyFilters()
                },
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
                        text = errorMessage ?: "Erro desconhecido",
                        modifier = Modifier.padding(16.dp)
                    )
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
                                isFavorite = false,
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
        }
    }
}
