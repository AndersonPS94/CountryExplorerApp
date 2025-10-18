package com.desafiodevspace.countryexplorer.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FavoritesScreen(
    favorites: List<String> = emptyList(),
    onCountryClick: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(modifier = modifier) {
        items(favorites) { countryName ->
            Text(
                text = countryName,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { onCountryClick(countryName) }
            )
        }
    }
}
