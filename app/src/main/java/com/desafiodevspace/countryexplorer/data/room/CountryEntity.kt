package com.desafiodevspace.countryexplorer.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey val code: String,
    val name: String,
    val flag: String,
    val region: String,
    val population: Long,
    val capital: String?,
    val languages: String?,
    val currencies: String?,
    val borders: String?
)
