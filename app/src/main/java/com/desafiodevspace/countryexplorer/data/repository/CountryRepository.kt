package com.desafiodevspace.countryexplorer.data.repository

import com.desafiodevspace.countryexplorer.data.model.Country
import com.desafiodevspace.countryexplorer.data.network.RetrofitInstance
import retrofit2.HttpException
import java.io.IOException
import java.net.URLEncoder
import kotlin.collections.firstOrNull

class CountryRepository {

    suspend fun getAllCountries(): Result<List<Country>> {
        return safeApiCall {
            RetrofitInstance.api.getAllCountries(
                fields = "name,flags,region,cca3"
            )
        }
    }

    suspend fun getCountryByName(name: String): Result<List<Country>> {
        if (name.isBlank()) {
            return Result.success(emptyList())
        }

        val encodedName = URLEncoder.encode(name, "UTF-8")
        return safeApiCall {
            RetrofitInstance.api.getCountryByName(
                name = encodedName,
                fields = "name,flags,region,cca3"
            )
        }
    }

    suspend fun getCountryByCode(code: String): Result<Country> {
        if (code.isBlank()) return Result.failure(Exception("Código do país vazio"))

        val encodedCode = URLEncoder.encode(code, "UTF-8")

        return safeApiCall {
            val countryList = RetrofitInstance.api.getCountryByCode(encodedCode)
            countryList.firstOrNull() ?: throw Exception("País com código $code não encontrado.")
        }
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return try {
            Result.success(apiCall())
        } catch (e: IOException) {
            Result.failure(Exception("Erro de conexão. Verifique sua rede."))
        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                404 -> "Recurso não encontrado. Código HTTP: ${e.code()}"
                in 400..499 -> "Erro na requisição. Código HTTP: ${e.code()}"
                in 500..599 -> "Erro no servidor. Código HTTP: ${e.code()}"
                else -> "Erro HTTP desconhecido: ${e.code()}"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            if (e.message?.contains("BEGIN_OBJECT") == true) {
                return Result.failure(Exception("Nenhum país encontrado para o termo fornecido."))
            }
            Result.failure(e)
        }
    }
}