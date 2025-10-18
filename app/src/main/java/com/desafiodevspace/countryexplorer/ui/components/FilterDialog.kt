package com.desafiodevspace.countryexplorer.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text("Filters", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            Text("Filter by Region", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.Absolute.spacedBy(8.dp)
            ) {
                val regions = listOf("Africa", "Americas", "Asia", "Europe", "Oceania")
                regions.forEach { region ->
                    FilterChip(
                        selected = selectedRegion == region,
                        onClick = { onSelectRegion(if (selectedRegion == region) null else region) },
                        label = { Text(region) },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE3F2FD),
                            selectedLabelColor = Color(0xFF1976D2),
                            containerColor = Color(0xFFF5F5F5),
                            labelColor = Color.Black
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (selectedRegion == region) Color(0xFF1976D2) else Color.LightGray
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Filter by Population", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.Absolute.spacedBy(8.dp)
            ) {
                val pops = listOf("<1M", "1M-10M", "10M-100M", ">100M")
                pops.forEach { pop ->
                    FilterChip(
                        selected = selectedPopulation == pop,
                        onClick = { onSelectPopulation(if (selectedPopulation == pop) null else pop) },
                        label = { Text(pop) },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE3F2FD),
                            selectedLabelColor = Color(0xFF1976D2),
                            containerColor = Color(0xFFF5F5F5),
                            labelColor = Color.Black
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (selectedPopulation == pop) Color(0xFF1976D2) else Color.LightGray
                        )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // "Clear Filters" button (light gray)
                OutlinedButton(
                    onClick = onClear,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFFF5F5F5),
                        contentColor = Color(0xFF555555)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Clear Filters")
                }

                // "Apply Filters" button (blue)
                Button(
                    onClick = onApply,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E88E5),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply Filters")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterBottomSheetPreview() {
    MaterialTheme {
        var selectedRegion by remember { mutableStateOf<String?>("Asia") }
        var selectedPopulation by remember { mutableStateOf<String?>("10M-100M") }

        FilterBottomSheet(
            selectedRegion = selectedRegion,
            selectedPopulation = selectedPopulation,
            onSelectRegion = { selectedRegion = it },
            onSelectPopulation = { selectedPopulation = it },
            onApply = {},
            onClear = {
                selectedRegion = null
                selectedPopulation = null
            }
        )
    }
}
