package com.desafiodevspace.countryexplorer.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    selectedRegion: String?,
    selectedPopulation: String?,
    onSelectRegion: (String?) -> Unit,
    onSelectPopulation: (String?) -> Unit,
    onApply: () -> Unit,
    onClear: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Filters", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Text("Filter by Region", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        FlowRow(
            horizontalArrangement = Arrangement.Absolute.spacedBy(8.dp)
        ) {
            val regions = listOf("Africa", "Americas", "Asia", "Europe", "Oceania")
            regions.forEach { region ->
                FilterChip(
                    selected = selectedRegion == region,
                    onClick = { onSelectRegion(if (selectedRegion == region) null else region) },
                    label = { Text(region) }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text("Filter by Population", fontWeight = FontWeight.Bold)
        FlowRow(
            horizontalArrangement = Arrangement.Absolute.spacedBy(8.dp)
        ) {
            val pops = listOf("<1M", "1M-10M", "10M-100M", ">100M")
            pops.forEach { pop ->
                FilterChip(
                    selected = selectedPopulation == pop,
                    onClick = { onSelectPopulation(if (selectedPopulation == pop) null else pop) },
                    label = { Text(pop) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(onClick = onClear) { Text("Clear Filters") }
            OutlinedButton(onClick = onApply) { Text("Apply Filters") }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterBottomSheetPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            var selectedRegion by remember { mutableStateOf<String?>("Africa") }
            var selectedPopulation by remember { mutableStateOf<String?>("10M-100M") }

            FilterBottomSheet(
                selectedRegion = selectedRegion,
                selectedPopulation = selectedPopulation,
                onSelectRegion = { selectedRegion = it },
                onSelectPopulation = { selectedPopulation = it },
                onApply = { /* test action */ },
                onClear = {
                    selectedRegion = null
                    selectedPopulation = null
                }
            )
        }
    }
}