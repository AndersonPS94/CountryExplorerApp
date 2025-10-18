package com.desafiodevspace.countryexplorer.navigation

sealed class Routes(val route: String) {
    object CountryList : Routes("country_list")
    object Favorites : Routes("favorites")
    object CountryDetail : Routes("country_detail/{countryCode}") {
        fun createRoute(code: String) = "country_detail/$code"
    }
}
