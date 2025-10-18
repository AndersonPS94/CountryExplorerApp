package com.desafiodevspace.countryexplorer.data.network

import com.desafiodevspace.countryexplorer.data.model.Country
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("all")
    suspend fun getAllCountries(): List<Country>

    @GET("name/{name}")
    suspend fun getCountryByName(@Path("name") name: String): List<Country>

    @GET("alpha/{code}")
    suspend fun getCountryByCode(@Path("code") code: String): Country
}
