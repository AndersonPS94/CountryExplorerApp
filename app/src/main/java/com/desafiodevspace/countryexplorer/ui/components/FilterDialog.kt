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
        Text("Filtros", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Text("Filtrar por Região", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        FlowRow(
            horizontalArrangement = Arrangement.Absolute.spacedBy(8.dp)
        ) {
            val regions = listOf("África", "Américas", "Ásia", "Europa", "Oceania")
            regions.forEach { region ->
                FilterChip(
                    selected = selectedRegion == region,
                    onClick = { onSelectRegion(if (selectedRegion == region) null else region) },
                    label = { Text(region) }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text("Filtrar por População", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
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
            OutlinedButton(onClick = onClear) { Text("Limpar Filtros") }
            OutlinedButton(onClick = onApply) { Text("Aplicar Filtros") }
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
            // Agora os estados são String? e podem receber null
            var selectedRegion by remember { mutableStateOf<String?>("África") }
            var selectedPopulation by remember { mutableStateOf<String?>("10M-100M") }

            FilterBottomSheet(
                selectedRegion = selectedRegion,
                selectedPopulation = selectedPopulation,
                onSelectRegion = { selectedRegion = it },
                onSelectPopulation = { selectedPopulation = it },
                onApply = { /* ação de teste */ },
                onClear = {
                    selectedRegion = null
                    selectedPopulation = null
                }
            )
        }
    }
}
