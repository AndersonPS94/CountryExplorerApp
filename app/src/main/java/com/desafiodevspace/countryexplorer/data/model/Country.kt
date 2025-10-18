package com.desafiodevspace.countryexplorer.data.model


data class Country(
    val name: Name,         // Name é outro data class
    val flags: Flags,       // Flags é outro data class
    val region: String,
    val population: Long,
    val capital: List<String>?,
    val languages: Map<String, String>?,
    val currencies: Map<String, Currency>?,
    val borders: List<String>?,
    val cca3: String
)

data class Name(val common: String)
data class Flags(val png: String)
data class Currency(val name: String, val symbol: String)
