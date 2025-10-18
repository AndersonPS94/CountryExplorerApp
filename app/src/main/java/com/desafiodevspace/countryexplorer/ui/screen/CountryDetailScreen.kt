package com.desafiodevspace.countryexplorer.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.desafiodevspace.countryexplorer.ui.viewmodel.CountryViewModel

@Composable
fun CountryDetailScreen(
    countryCode: String,
    viewModel: CountryViewModel = (viewModel()),
    onBack: () -> Unit
) {
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(countryCode) {
        viewModel.fetchCountryByCode(countryCode)
    }

    Scaffold(
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                errorMessage != null -> Text(
                    text = errorMessage ?: "",
                    modifier = Modifier.align(Alignment.Center)
                )
                selectedCountry != null -> {
                    val country = selectedCountry!!
                    Column(modifier = Modifier.padding(16.dp)) {
                        AsyncImage(
                            model = country.flags.png,
                            contentDescription = "${country.name.common} flag",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Name: ${country.name.common}")
                        Text(text = "Capital: ${country.capital?.joinToString() ?: "N/A"}")
                        Text(text = "Population: ${country.population}")
                        Text(text = "Region: ${country.region}")
                        Text(text = "Languages: ${country.languages?.values?.joinToString() ?: "N/A"}")
                        Text(text = "Currencies: ${country.currencies?.values?.joinToString { it.name } ?: "N/A"}")
                        Text(text = "Borders: ${country.borders?.joinToString() ?: "None"}")
                    }
                }
            }
        }
    }
}
