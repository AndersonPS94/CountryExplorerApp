package com.desafiodevspace.countryexplorer.data.mapper

import com.desafiodevspace.countryexplorer.data.model.Country
import com.desafiodevspace.countryexplorer.data.model.CountryUiModel
import com.desafiodevspace.countryexplorer.data.model.Currency
import com.desafiodevspace.countryexplorer.data.model.Flags
import com.desafiodevspace.countryexplorer.data.model.Name
import com.desafiodevspace.countryexplorer.data.room.CountryEntity

// Converte Country -> CountryEntity
fun Country.toEntity(): CountryEntity = CountryEntity(
    code = this.cca3,
    name = this.name.common,
    flag = this.flags.png,
    region = this.region,
    population = this.population,
    capital = this.capital?.joinToString(),
    languages = this.languages?.values?.joinToString(),
    currencies = this.currencies?.values?.joinToString { it.name },
    borders = this.borders?.joinToString()
)

// Converte CountryEntity -> Country
fun CountryEntity.toCountry(): Country = Country(
    cca3 = this.code,
    name = Name(this.name),
    flags = Flags(this.flag),
    region = this.region,
    population = this.population,
    capital = this.capital?.split(", ") ?: emptyList(),
    languages = this.languages?.split(", ")?.associateWith { it } ?: emptyMap(),
    currencies = this.currencies?.split(", ")?.associateWith { Currency(it, "") } ?: emptyMap(),
    borders = this.borders?.split(", ") ?: emptyList()
)

fun Country.toUiModel(): CountryUiModel = CountryUiModel(
    name = this.name.common,
    region = this.region,
    flag = this.flags.png,
    code = this.cca3,
    population = this.population
)