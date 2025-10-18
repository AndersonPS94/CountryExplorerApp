package com.desafiodevspace.countryexplorer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search icon",
                tint = Color(0xFF9E9E9E)
            )
        },
        placeholder = {
            Text(
                text = "Search for a country...",
                color = Color(0xFF9E9E9E)
            )
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF9FBFD),
            unfocusedContainerColor = Color(0xFFF9FBFD),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color(0xFF2196F3)
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier
                .background(Color(0xFFF0F4F8))
                .padding(16.dp)
        ) {
            var query by remember { mutableStateOf("") }
            SearchBar(
                query = query,
                onQueryChange = { query = it }
            )
        }
    }
}
