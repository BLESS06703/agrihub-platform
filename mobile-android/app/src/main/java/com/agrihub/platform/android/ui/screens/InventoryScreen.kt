package com.agrihub.platform.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agrihub.platform.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory", fontWeight = FontWeight.Bold) },
                actions = { TextButton(onClick = { }) { Text("+ Add", color = ForestGreen) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhite)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(OffWhite).padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = SurfaceWhite), shape = RoundedCornerShape(12.dp)) {
                    Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Text("78", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                            Text("Total Items", fontSize = 11.sp, color = SlateGrey)
                        }
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Text("6", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ErrorRed)
                            Text("Low Stock", fontSize = 11.sp, color = SlateGrey)
                        }
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Text("MWK 850K", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = HarvestGold)
                            Text("Total Value", fontSize = 11.sp, color = SlateGrey)
                        }
                    }
                }
            }
            item { InventoryItemCard("NPK Fertilizer", "FERTILIZER", "250 kg", "KG", "In Stock") }
            item { InventoryItemCard("Maize Seed SC719", "SEEDS", "50 kg", "KG", "In Stock") }
            item { InventoryItemCard("Pesticide - Cypermethrin", "PESTICIDE", "15 L", "LITER", "Low Stock [!]") }
            item { InventoryItemCard("Diesel", "FUEL", "200 L", "LITER", "In Stock") }
            item { InventoryItemCard("Feed - Grower Meal", "FEED", "500 kg", "KG", "In Stock") }
        }
    }
}

@Composable
fun InventoryItemCard(name: String, category: String, quantity: String, unit: String, status: String) {
    Card(colors = CardDefaults.cardColors(containerColor = SurfaceWhite), shape = RoundedCornerShape(12.dp)) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(name, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Charcoal)
                Text("$category    $quantity", fontSize = 13.sp, color = SlateGrey)
            }
            Text(status, fontSize = 12.sp, color = if (status.contains("[!]")) ErrorRed else ForestGreen)
        }
    }
}
