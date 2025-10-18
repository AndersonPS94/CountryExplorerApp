package com.desafiodevspace.countryexplorer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.desafiodevspace.countryexplorer.data.model.Country
import com.desafiodevspace.countryexplorer.data.model.CountryUiModel
import com.desafiodevspace.countryexplorer.data.repository.CountryRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CountryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CountryRepository(application.applicationContext)

    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries: StateFlow<List<Country>> = _countries

    private val countriesUi: StateFlow<List<CountryUiModel>> = _countries
        .map { countryList ->
            countryList.mapNotNull { country ->
                val name = country.name.common
                val flag = country.flags.png
                val region = country.region

                if (name.isBlank() || flag.isBlank() || region.isBlank()) return@mapNotNull null

                CountryUiModel(
                    name = name,
                    region = region,
                    flag = flag,
                    code = country.cca3,
                    population = country.population
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

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
        }.combine(_selectedPopulation) { list, populationRange ->
            list.filter { populationRange == null || populationMatches(it.population, populationRange) }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateRegionFilter(region: String?) {
        _selectedRegion.value = region
    }

    fun updatePopulationFilter(populationRange: String?) {
        _selectedPopulation.value = populationRange
    }

    init {
        fetchAllCountries()
    }

    fun fetchAllCountries() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _countries.value = emptyList()

            val result = repository.getAllCountriesRemote()
            result.onSuccess { list ->
                if (list.isNotEmpty()) {
                    _countries.value = list.sortedBy { country: Country -> country.name.common }
                } else {
                    _errorMessage.value = "Nenhum país encontrado."
                }
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Erro ao carregar países."
            }

            _isLoading.value = false
        }
    }

    fun fetchCountryByCode(code: String) {
        viewModelScope.launch {
            if (code.isBlank()) {
                _errorMessage.value = "Código do país inválido."
                return@launch
            }

            _isLoading.value = true
            _errorMessage.value = null
            _selectedCountry.value = null
            _borderCountries.value = emptyList()

            val result = repository.getCountryByCodeRemote(code)
            result.onSuccess { country ->
                _selectedCountry.value = country

                country.borders?.let { borderCodes ->
                    if (borderCodes.isNotEmpty()) {
                        fetchBorderCountries(borderCodes)
                    }
                }
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Erro ao carregar país."
            }

            _isLoading.value = false
        }
    }

    private fun fetchBorderCountries(borderCodes: List<String>) {
        viewModelScope.launch {
            val fetchedCountries = mutableListOf<Country>()

            val deferredCountries = borderCodes.map { borderCode ->
                async {
                    repository.getCountryByCodeRemote(borderCode)
                }
            }

            deferredCountries.awaitAll().forEach { result ->
                result.onSuccess { country ->
                    fetchedCountries.add(country)
                }.onFailure { e ->
                    println("Erro ao carregar vizinho: ${e.message}")
                }
            }

            _borderCountries.value = fetchedCountries.sortedBy { country: Country -> country.name.common }
        }
    }
}
