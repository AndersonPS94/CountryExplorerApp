package com.desafiodevspace.countryexplorer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desafiodevspace.countryexplorer.data.model.Country
import com.desafiodevspace.countryexplorer.data.repository.CountryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CountryViewModel(
    private val repository: CountryRepository = CountryRepository()
) : ViewModel() {

    //listagem de paises
    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries: StateFlow<List<Country>> = _countries

    //carregamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    //errp
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    //pais selecionado -> pais detalhes
    private val _selectedCountry = MutableStateFlow<Country?>(null)
    val selectedCountry: StateFlow<Country?> = _selectedCountry

    init {
        fetchAllCountries()
    }

    // funcao que busca todos os paises
    fun fetchAllCountries() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.getAllCountries()
            if (result.isSuccess) {
                _countries.value = result.getOrDefault(emptyList())
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Erro desconhecido"
            }
            _isLoading.value = false
        }
    }

    //Busca o pais pelo nome
    fun searchCountriesByName(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.getCountryByName(name)
            if (result.isSuccess) {
                _countries.value = result.getOrDefault(emptyList())
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Erro desconhecido"
            }
            _isLoading.value = false
        }
    }

    //Buscar país pelo codigo(para os vizinhos)
    fun fetchCountryByCode(code: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.getCountryByCode(code)
            if (result.isSuccess) {
                _selectedCountry.value = result.getOrNull()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Erro desconhecido"
            }
            _isLoading.value = false
        }
    }

    // Selecionar pais manualmente
    fun selectCountry(country: Country) {
        _selectedCountry.value = country
    }
}
