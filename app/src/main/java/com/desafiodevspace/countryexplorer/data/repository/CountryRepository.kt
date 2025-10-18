package com.desafiodevspace.countryexplorer.data.repository

import android.content.Context
import com.desafiodevspace.countryexplorer.data.model.Country
import com.desafiodevspace.countryexplorer.data.model.Currency
import com.desafiodevspace.countryexplorer.data.model.Flags
import com.desafiodevspace.countryexplorer.data.model.Name
import com.desafiodevspace.countryexplorer.data.network.RetrofitInstance
import com.desafiodevspace.countryexplorer.data.room.AppDatabase
import com.desafiodevspace.countryexplorer.data.room.CountryEntity
import com.desafiodevspace.countryexplorer.data.room.FavoriteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import java.net.URLEncoder
import kotlin.collections.emptyList

class CountryRepository(context: Context) {

    private val countryDao = AppDatabase.getInstance(context).countryDao()
    private val favoriteDao = AppDatabase.getInstance(context).favoriteDao()



    /** ------------------ OFFLINE FIRST ------------------ **/
    fun getAllCountriesLocal(): Flow<List<Country>> =
        countryDao.getAllCountries().map { list -> list.map { it.toCountry() } }

    fun getCountryByCodeLocal(code: String): Flow<Country?> =
        countryDao.getCountryByCode(code).map { it?.toCountry() }

    /** ------------------ FAVORITOS ------------------ **/
    fun getFavorites(): Flow<Set<String>> =
        favoriteDao.getAllFavorites().map { list -> list.map { it.countryCode }.toSet() }

    suspend fun addFavorite(code: String) {
        favoriteDao.insert(FavoriteEntity(code))
    }

    suspend fun removeFavorite(code: String) {
        favoriteDao.delete(FavoriteEntity(code))
    }

    suspend fun toggleFavorite(code: String) {
        val current = favoriteDao.getAllFavorites().firstOrNull()?.map { it.countryCode } ?: emptyList()
        if (current.contains(code)) removeFavorite(code) else addFavorite(code)
    }

    /** ------------------ API ------------------ **/
    suspend fun getAllCountriesRemote(): Result<List<Country>> = safeApiCall {
        val list = RetrofitInstance.api.getAllCountries(
            fields = "name,flags,region,population,capital,languages,currencies,borders,cca3"
        )
        countryDao.insertAll(list.map { it.toEntity() })
        list
    }

    suspend fun getCountryByCodeRemote(code: String): Result<Country> {
        if (code.isBlank()) return Result.failure(Exception("Código do país vazio"))
        val encodedCode = URLEncoder.encode(code, "UTF-8")
        return safeApiCall {
            val countries: List<Country> = RetrofitInstance.api.getCountryByCode(encodedCode)
            countryDao.insertAll(countries.map { it.toEntity() })
            countries.firstOrNull() ?: throw Exception("Nenhum país encontrado para o código $code")
        }
    }

    /** ------------------ SAFECALL ------------------ **/
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

/** ------------------ EXTENSIONS ------------------ **/
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
