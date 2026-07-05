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
fun MarketScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(OffWhite),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Text("Marketplace", style = MaterialTheme.typography.headlineMedium, color = ModuleMarketplace) }
        item { ListingCard("Maize", "Grade A", "500 kg", "MWK 800/kg") }
        item { ListingCard("Soybeans", "Grade B", "1,200 kg", "MWK 1,200/kg") }
        item { ListingCard("Groundnuts", "Grade A", "300 kg", "MWK 1,500/kg") }
    }
}

@Composable
fun ListingCard(crop: String, grade: String, quantity: String, price: String) {
    Card(colors = CardDefaults.cardColors(containerColor = SurfaceWhite)) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(crop, style = MaterialTheme.typography.titleMedium, color = Charcoal)
                Text("$grade - $quantity", style = MaterialTheme.typography.bodySmall, color = SlateGrey)
            }
            Text(price, style = MaterialTheme.typography.titleMedium, color = ForestGreen)
        }
    }
}
