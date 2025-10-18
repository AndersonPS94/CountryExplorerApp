package com.desafiodevspace.countryexplorer.viewmodel

import android.Manifest
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.desafiodevspace.countryexplorer.data.model.Country
import com.desafiodevspace.countryexplorer.data.model.CountryUiModel
import com.desafiodevspace.countryexplorer.data.repository.CountryRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CountryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CountryRepository(application.applicationContext)

    val countries: StateFlow<List<Country>> = repository.getAllCountriesLocal()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val countriesUi: StateFlow<List<CountryUiModel>> = countries.map { list ->
        list.mapNotNull { country ->
            val name = country.name.common
            val flag = country.flags.png
            val region = country.region
            if (name.isBlank() || flag.isBlank() || region.isBlank()) return@mapNotNull null
            CountryUiModel(name, region, flag, country.cca3, country.population)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val favorites: StateFlow<Set<String>> = repository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    fun toggleFavorite(code: String) = viewModelScope.launch {
        repository.toggleFavorite(code)
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _selectedCountry = MutableStateFlow<Country?>(null)
    val selectedCountry: StateFlow<Country?> = _selectedCountry

    private val _borderCountries = MutableStateFlow<List<Country>>(emptyList())
    val borderCountries: StateFlow<List<Country>> = _borderCountries

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedRegion = MutableStateFlow<String?>(null)
    val selectedRegion: StateFlow<String?> = _selectedRegion

    private val _selectedPopulation = MutableStateFlow<String?>(null)
    val selectedPopulation: StateFlow<String?> = _selectedPopulation

    private fun populationMatches(population: Long, range: String?): Boolean {
        val ONE_MILLION = 1_000_000L
        val TEN_MILLION = 10_000_000L
        val HUNDRED_MILLION = 100_000_000L
        return when (range) {
            "<1M" -> population < ONE_MILLION
            "1M-10M" -> population in ONE_MILLION until TEN_MILLION
            "10M-100M" -> population in TEN_MILLION until HUNDRED_MILLION
            ">100M" -> population >= HUNDRED_MILLION
            else -> true
        }
    }

    val filteredCountries: StateFlow<List<CountryUiModel>> =
        countriesUi.combine(_searchQuery) { list, query ->
            list.filter { it.name.contains(query, ignoreCase = true) }
        }.combine(_selectedRegion) { list, region ->
            list.filter { region == null || it.region == region }
        }.combine(_selectedPopulation) { list, range ->
            list.filter { range == null || populationMatches(it.population, range) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun updateRegionFilter(region: String?) { _selectedRegion.value = region }
    fun updatePopulationFilter(range: String?) { _selectedPopulation.value = range }

    init {
        if (isOnline()) refreshCountries()
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun isOnline(): Boolean {
        val cm = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun refreshCountries() {
        viewModelScope.launch {
            try {
                val result = repository.getAllCountriesRemote()
                result.onFailure {
                    println("Unable to refresh from server: ${it.message}")
                }
            } catch (e: Exception) {
                println("Error updating countries: ${e.message}")
            }
        }
    }

    fun fetchCountryByCode(code: String) {
        viewModelScope.launch {
            if (code.isBlank()) {
                _errorMessage.value = "Invalid country code.";
                return@launch
            }

            _isLoading.value = true
            _errorMessage.value = null
            _selectedCountry.value = null
            _borderCountries.value = emptyList()

            _selectedCountry.value = repository.getCountryByCodeLocal(code).firstOrNull()

            if (isOnline()) {
                val result = repository.getCountryByCodeRemote(code)
                result.onSuccess { country ->
                    _selectedCountry.value = country
                    country.borders?.let { fetchBorderCountries(it) }
                }.onFailure {
                    println("Unable to update country: ${it.message}")
                }
            }

            _isLoading.value = false
        }
    }

    private fun fetchBorderCountries(borderCodes: List<String>) {
        viewModelScope.launch {
            val localCountries = borderCodes.mapNotNull { code ->
                repository.getCountryByCodeLocal(code).firstOrNull()
            }.toMutableList()

            val missingCodes = borderCodes.filter { code ->
                localCountries.none { it.cca3 == code }
            }

            val deferred = missingCodes.map { code -> async { repository.getCountryByCodeRemote(code) } }
            deferred.awaitAll().forEach { it.onSuccess { localCountries.add(it) } }

            _borderCountries.value = localCountries.sortedBy { it.name.common }
        }
    }
}
