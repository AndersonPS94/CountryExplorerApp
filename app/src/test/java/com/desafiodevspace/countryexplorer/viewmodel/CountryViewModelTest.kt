package com.desafiodevspace.countryexplorer.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.desafiodevspace.countryexplorer.data.model.*
import com.desafiodevspace.countryexplorer.data.repository.CountryRepository
import io.mockk.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CountryViewModelTest {

    private lateinit var application: Application
    private lateinit var viewModel: CountryViewModel
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var network: Network
    private lateinit var networkCapabilities: NetworkCapabilities

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

        application = mockk(relaxed = true)
        connectivityManager = mockk(relaxed = true)
        network = mockk(relaxed = true)
        networkCapabilities = mockk(relaxed = true)

        every { application.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true

        mockkConstructor(CountryRepository::class)
        every { anyConstructed<CountryRepository>().getAllCountriesLocal() } returns flow { emit(emptyList()) }
        every { anyConstructed<CountryRepository>().getFavorites() } returns flow { emit(emptySet()) }
        coEvery { anyConstructed<CountryRepository>().toggleFavorite(any()) } just Runs
        every { anyConstructed<CountryRepository>().getCountryByCodeLocal(any()) } returns emptyFlow()
        coEvery { anyConstructed<CountryRepository>().getCountryByCodeRemote(any()) } returns Result.failure(Exception("Not implemented"))
        coEvery { anyConstructed<CountryRepository>().getAllCountriesRemote() } returns Result.success(emptyList())

        viewModel = CountryViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `test initial state`() = runTest {
        assertTrue(viewModel.countries.value.isEmpty())
        assertTrue(viewModel.countriesUi.value.isEmpty())
        assertTrue(viewModel.favorites.value.isEmpty())
        assertTrue(viewModel.filteredCountries.value.isEmpty())
        assertEquals(false, viewModel.isLoading.value)
        assertEquals(null, viewModel.errorMessage.value)
        assertEquals(null, viewModel.selectedCountry.value)
        assertTrue(viewModel.borderCountries.value.isEmpty())
    }

    @Test
    fun `update filters update state correctly`() = runTest {
        viewModel.updateSearchQuery("Brazil")
        viewModel.updateRegionFilter("Americas")
        viewModel.updatePopulationFilter("1M-10M")

        assertEquals("Brazil", viewModel.searchQuery.value)
        assertEquals("Americas", viewModel.selectedRegion.value)
        assertEquals("1M-10M", viewModel.selectedPopulation.value)
    }

    @Test
    fun `fetchCountryByCode handles invalid code`() = runTest {
        viewModel.fetchCountryByCode("")
        advanceUntilIdle()
        assertEquals("Invalid country code.", viewModel.errorMessage.value)
    }

    @Test
    fun `fetchCountryByCode calls repository for valid code`() = runTest {
        val country = Country(
            cca3 = "BR",
            name = Name("Brazil"),
            flags = Flags("🇧🇷"),
            region = "Americas",
            population = 211000000,
            capital = listOf("Brasília"),
            languages = mapOf("pt" to "Portuguese"),
            currencies = mapOf("BRL" to Currency("Real", "")),
            borders = listOf("ARG")
        )

        every { anyConstructed<CountryRepository>().getCountryByCodeLocal("BR") } returns flow { emit(country) }
        coEvery { anyConstructed<CountryRepository>().getCountryByCodeRemote("BR") } returns Result.success(country)

        viewModel.fetchCountryByCode("BR")
        advanceUntilIdle()

        assertEquals(country, viewModel.selectedCountry.value)
    }

    @Test
    fun `refreshCountries calls repository`() = runTest {
        viewModel.refreshCountries()
        advanceUntilIdle()
        coVerify { anyConstructed<CountryRepository>().getAllCountriesRemote() }
    }
}
