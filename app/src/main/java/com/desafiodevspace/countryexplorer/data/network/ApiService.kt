package com.desafiodevspace.countryexplorer.data.network

import com.desafiodevspace.countryexplorer.data.model.Country
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("all")
    suspend fun getAllCountries(
        @Query("fields") fields: String = "name,flags,region,cca3"
    ): List<Country>

    @GET("name/{name}")
    suspend fun getCountryByName(
        @Path("name") name: String,
        @Query("fields") fields: String = "name,flags,region,cca3"
    ): List<Country>

    @GET("alpha/{code}")
    suspend fun getCountryByCode(
        @Path("code") code: String,
    ): List<Country>
}