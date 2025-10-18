package com.desafiodevspace.countryexplorer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desafiodevspace.countryexplorer.data.model.Country
import com.desafiodevspace.countryexplorer.data.repository.CountryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class CountryViewModel(
    private val repository: CountryRepository = CountryRepository()
) : ViewModel() {

    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries: StateFlow<List<Country>> = _countries

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _selectedCountry = MutableStateFlow<Country?>(null)
    val selectedCountry: StateFlow<Country?> = _selectedCountry

    private val _borderCountries = MutableStateFlow<List<Country>>(emptyList())
    val borderCountries: StateFlow<List<Country>> = _borderCountries


    init {
        fetchAllCountries()
    }

    fun fetchAllCountries() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _countries.value = emptyList()

            val result = repository.getAllCountries()
            result.onSuccess { list ->
                if (list.isNotEmpty()) {
                    _countries.value = list.sortedBy { it.name.common }
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

            val result = repository.getCountryByCode(code)
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
                    repository.getCountryByCode(borderCode)
                }
            }

            deferredCountries.awaitAll().forEach { result ->
                result.onSuccess { country ->
                    fetchedCountries.add(country)
                }.onFailure { e ->
                    println("Erro ao carregar vizinho: ${e.message}")
                }
            }

            _borderCountries.value = fetchedCountries.sortedBy { it.name.common }
        }
    }
}