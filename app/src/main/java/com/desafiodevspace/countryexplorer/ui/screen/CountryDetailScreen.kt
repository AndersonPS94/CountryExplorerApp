package com.desafiodevspace.countryexplorer.ui.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.desafiodevspace.countryexplorer.data.model.Country
import com.desafiodevspace.countryexplorer.viewmodel.CountryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDetailScreen(
    countryCode: String,
    viewModel: CountryViewModel = viewModel(),
    onBack: () -> Unit,
    onNavigateToCountryDetail: (String) -> Unit
) {
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(countryCode) {
        viewModel.fetchCountryByCode(countryCode)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Country Details")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                isLoading && selectedCountry == null -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                !errorMessage.isNullOrEmpty() -> {
                    Text(
                        text = errorMessage ?: "Unknown error",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                selectedCountry != null -> {
                    CountryDetailsContent(
                        country = selectedCountry!!,
                        scrollState = scrollState,
                        viewModel = viewModel,
                        onNavigateToCountryDetail = onNavigateToCountryDetail
                    )
                }
            }
        }
    }
}

@Composable
fun CountryDetailsContent(
    country: Country,
    scrollState: ScrollState,
    viewModel: CountryViewModel,
    onNavigateToCountryDetail: (String) -> Unit
) {
    val borderCountries by viewModel.borderCountries.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Country Name
        Text(
            text = country.name.common,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            AsyncImage(
                model = country.flags.png,
                contentDescription = "${country.name.common} flag",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailItem(
                    label = "Capital",
                    value = country.capital?.joinToString() ?: "N/A"
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                DetailItem(
                    label = "Population",
                    value = country.population.takeIf { it > 0 }
                        ?.let { String.format("%,d", it) } ?: "N/A"
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                DetailItem(
                    label = "Region",
                    value = country.region
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                DetailItem(
                    label = "Languages",
                    value = country.languages?.values?.joinToString() ?: "N/A"
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                DetailItem(
                    label = "Currencies",
                    value = country.currencies?.values?.joinToString { it.name } ?: "N/A"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!country.borders.isNullOrEmpty()) {
            Text(
                text = "Neighboring Countries",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (borderCountries.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(24.dp)
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(borderCountries) { borderCountry ->
                        AssistChip(
                            onClick = { onNavigateToCountryDetail(borderCountry.cca3) },
                            shape = MaterialTheme.shapes.large,
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = borderCountry.flags.png,
                                        contentDescription = "${borderCountry.name.common} flag",
                                        modifier = Modifier.size(24.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(borderCountry.name.common)
                                }
                            },
                            elevation = AssistChipDefaults.assistChipElevation()
                        )
                    }
                }
            }
        } else {
            Text(
                text = "No neighboring countries.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
