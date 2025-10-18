package com.desafiodevspace.countryexplorer.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CountryDetailScreen(
    countryCode: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "Detalhes do país: $countryCode")
        Button(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
            Text("Voltar")
        }
    }
}
