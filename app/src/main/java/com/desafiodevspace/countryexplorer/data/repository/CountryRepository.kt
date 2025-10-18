package com.desafiodevspace.countryexplorer.data.repository

import com.desafiodevspace.countryexplorer.data.model.Country
import com.desafiodevspace.countryexplorer.data.network.RetrofitInstance
import retrofit2.HttpException
import java.io.IOException
import java.net.URLEncoder

class CountryRepository {

    suspend fun getAllCountries(): Result<List<Country>> {
        return safeApiCall { RetrofitInstance.api.getAllCountries() }
    }

    suspend fun getCountryByName(name: String): Result<List<Country>> {
        val encodedName = URLEncoder.encode(name, "UTF-8")
        return safeApiCall { RetrofitInstance.api.getCountryByName(encodedName) }
    }

    suspend fun getCountryByCode(code: String): Result<Country> {
        if (code.isBlank()) return Result.failure(Exception("Código do país vazio"))
        return safeApiCall { RetrofitInstance.api.getCountryByCode(code) }
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return try {
            Result.success(apiCall())
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(Exception("Erro HTTP ${e.code()}: ${e.message()}"))
        }
    }
}
