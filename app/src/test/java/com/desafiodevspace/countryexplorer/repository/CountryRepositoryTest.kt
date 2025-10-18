package com.desafiodevspace.countryexplorer.repository

import android.content.Context
import app.cash.turbine.test
import com.desafiodevspace.countryexplorer.data.model.Country
import com.desafiodevspace.countryexplorer.data.model.Currency
import com.desafiodevspace.countryexplorer.data.model.Flags
import com.desafiodevspace.countryexplorer.data.model.Name
import com.desafiodevspace.countryexplorer.data.network.RetrofitInstance
import com.desafiodevspace.countryexplorer.data.repository.CountryRepository
import com.desafiodevspace.countryexplorer.data.room.AppDatabase
import com.desafiodevspace.countryexplorer.data.room.CountryDao
import com.desafiodevspace.countryexplorer.data.room.FavoriteDao
import com.desafiodevspace.countryexplorer.data.room.CountryEntity
import com.desafiodevspace.countryexplorer.data.room.FavoriteEntity
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class CountryRepositoryTest {

    private lateinit var countryDao: CountryDao
    private lateinit var favoriteDao: FavoriteDao
    private lateinit var repository: CountryRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        countryDao = mockk()
        favoriteDao = mockk()
        val context = mockk<Context>()
        // Mock do AppDatabase
        mockkObject(AppDatabase)
        every { AppDatabase.getInstance(context).countryDao() } returns countryDao
        every { AppDatabase.getInstance(context).favoriteDao() } returns favoriteDao

        repository = CountryRepository(context)
    }

    /** ------------------ TESTES OFFLINE ------------------ **/
    @Test
    fun `getAllCountriesLocal deve retornar lista de countries do dao`() = runTest {
        val countryEntity = CountryEntity(
            code = "BR", name = "Brazil", flag = "🇧🇷", region = "Americas",
            population = 211000000, capital = "Brasília", languages = "Portuguese",
            currencies = "Real", borders = "ARG,URY"
        )
        every { countryDao.getAllCountries() } returns flow { emit(listOf(countryEntity)) }

        repository.getAllCountriesLocal().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("BR", result[0].cca3)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getCountryByCodeLocal deve retornar country do dao`() = runTest {
        val countryEntity = CountryEntity(
            code = "BR", name = "Brazil", flag = "🇧🇷", region = "Americas",
            population = 211000000, capital = "Brasília", languages = "Portuguese",
            currencies = "Real", borders = "ARG,URY"
        )
        every { countryDao.getCountryByCode("BR") } returns flow { emit(countryEntity) }

        repository.getCountryByCodeLocal("BR").test {
            val result = awaitItem()
            assertEquals("Brazil", result?.name?.common)
            cancelAndConsumeRemainingEvents()
        }
    }

    /** ------------------ TESTES FAVORITOS ------------------ **/
    @Test
    fun `getFavorites deve retornar set de códigos`() = runTest {
        val favorites = listOf(FavoriteEntity("BR"), FavoriteEntity("US"))
        every { favoriteDao.getAllFavorites() } returns flow { emit(favorites) }

        repository.getFavorites().test {
            val result = awaitItem()
            assertTrue(result.contains("BR"))
            assertTrue(result.contains("US"))
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `toggleFavorite deve adicionar e remover corretamente`() = runTest {
        val favorites = listOf(FavoriteEntity("BR"))
        every { favoriteDao.getAllFavorites() } returns flow { emit(favorites) }
        coEvery { favoriteDao.insert(any()) } just Runs
        coEvery { favoriteDao.delete(any()) } just Runs

        repository.toggleFavorite("US") // não está nos favoritos → deve adicionar
        coVerify { favoriteDao.insert(FavoriteEntity("US")) }

        repository.toggleFavorite("BR") // está nos favoritos → deve remover
        coVerify { favoriteDao.delete(FavoriteEntity("BR")) }
    }

    /** ------------------ TESTES API ------------------ **/
    @Test
    fun `getAllCountriesRemote retorna sucesso`() = runTest {
        val country = Country(
            cca3 = "BR", name = Name("Brazil"), flags = Flags("🇧🇷"), region = "Americas",
            population = 211000000, capital = listOf("Brasília"), languages = mapOf("pt" to "Portuguese"),
            currencies = mapOf("BRL" to Currency("Real", "")), borders = listOf("ARG")
        )
        mockkObject(RetrofitInstance)
        coEvery { RetrofitInstance.api.getAllCountries(any()) } returns listOf(country)
        coEvery { countryDao.insertAll(any()) } just Runs

        val result = repository.getAllCountriesRemote()
        val countries = result.getOrNull() // Corrigido aqui
        assertTrue(countries?.isNotEmpty() == true)
        assertEquals("BR", countries?.firstOrNull()?.cca3)
    }

    @Test
    fun `getCountryByCodeRemote lança exceção para código vazio`() = runTest {
        val result = repository.getCountryByCodeRemote("")
        assertTrue(result.isFailure)
        assertEquals("Código do país vazio", result.exceptionOrNull()?.message)
    }

    @Test
    fun `safeApiCall captura IOException`() = runTest {
        mockkObject(RetrofitInstance)
        coEvery { RetrofitInstance.api.getAllCountries(any()) } throws IOException("Sem conexão")

        val result = repository.getAllCountriesRemote()
        assertTrue(result.isFailure)
        assertEquals("Erro de conexão. Verifique sua rede.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `safeApiCall captura HttpException 404`() = runTest {
        val httpException = mockk<HttpException>()
        every { httpException.code() } returns 404
        mockkObject(RetrofitInstance)
        coEvery { RetrofitInstance.api.getAllCountries(any()) } throws httpException

        val result = repository.getAllCountriesRemote()
        assertTrue(result.isFailure)
        assertEquals("Recurso não encontrado. Código HTTP: 404", result.exceptionOrNull()?.message)
    }
}
