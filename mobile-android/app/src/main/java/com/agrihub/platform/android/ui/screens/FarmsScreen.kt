package com.agrihub.platform.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agrihub.platform.android.ui.theme.*

@Composable
fun FarmsScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(OffWhite),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Text("My Farms", style = MaterialTheme.typography.headlineMedium, color = ForestGreen) }
        item { FarmCard("Mzuzu Coffee Farm", "5 fields", "12.5 ha") }
        item { FarmCard("Lilongwe Vegetable Farm", "3 fields", "8.2 ha") }
        item { FarmCard("Blantyre Mixed Farm", "8 fields", "25.0 ha") }
    }
}

@Composable
fun FarmCard(name: String, fields: String, area: String) {
    Card(colors = CardDefaults.cardColors(containerColor = SurfaceWhite)) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(name, style = MaterialTheme.typography.titleMedium, color = Charcoal)
                Text(fields, style = MaterialTheme.typography.bodySmall, color = SlateGrey)
            }
            Text(area, style = MaterialTheme.typography.titleMedium, color = ForestGreen)
        }
    }
}
