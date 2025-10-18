package com.desafiodevspace.countryexplorer.data.repository

import com.desafiodevspace.countryexplorer.data.model.Country
import com.desafiodevspace.countryexplorer.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class CountryRepository {

    suspend fun getAllCountries(): Result<List<Country>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getAllCountries()
                Result.success(response)
            } catch (e: IOException) {
                Result.failure(e) // Erro de rede
            } catch (e: HttpException) {
                Result.failure(e) // Erro HTTP
            }
        }
    }

    suspend fun getCountryByName(name: String): Result<List<Country>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getCountryByName(name)
                Result.success(response)
            } catch (e: IOException) {
                Result.failure(e)
            } catch (e: HttpException) {
                Result.failure(e)
            }
        }
    }

    suspend fun getCountryByCode(code: String): Result<Country> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getCountryByCode(code)
                Result.success(response)
            } catch (e: IOException) {
                Result.failure(e)
            } catch (e: HttpException) {
                Result.failure(e)
            }
        }
    }
}
