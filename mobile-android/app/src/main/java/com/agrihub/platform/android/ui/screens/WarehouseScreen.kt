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
fun WarehouseScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Warehouse", fontWeight = FontWeight.Bold) },
                actions = { TextButton(onClick = { }) { Text("+ Intake", color = ForestGreen) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhite)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(OffWhite).padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = SurfaceWhite), shape = RoundedCornerShape(12.dp)) {
                    Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Text("85%", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                            Text("Occupancy", fontSize = 11.sp, color = SlateGrey)
                        }
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Text("42", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ModuleWarehouse)
                            Text("Active Lots", fontSize = 11.sp, color = SlateGrey)
                        }
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Text("3", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = WarningOrange)
                            Text("Pending Dispatch", fontSize = 11.sp, color = SlateGrey)
                        }
                    }
                }
            }
            item { SectionHeader("Storage Lots") }
            item { WarehouseLotCard("LOT-2026-042", "Maize - Grade A", "2,500 kg", "Received: June 15", "IN_STORAGE") }
            item { WarehouseLotCard("LOT-2026-038", "Soybeans - Grade B", "1,800 kg", "Received: June 10", "IN_STORAGE") }
            item { WarehouseLotCard("LOT-2026-035", "Groundnuts - Grade A", "900 kg", "Received: June 5", "DISPATCHING") }
        }
    }
}

@Composable
fun WarehouseLotCard(lotNumber: String, produce: String, quantity: String, date: String, status: String) {
    val statusColor = when(status) { "IN_STORAGE" -> ForestGreen; "DISPATCHING" -> WarningOrange; else -> SlateGrey }
    Card(colors = CardDefaults.cardColors(containerColor = SurfaceWhite), shape = RoundedCornerShape(12.dp)) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(lotNumber, fontSize = 12.sp, color = SlateGrey, fontWeight = FontWeight.Medium)
                Text(produce, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Charcoal)
                Text("$quantity    $date", fontSize = 13.sp, color = SlateGrey)
            }
            Text(status.replace("_", " "), fontSize = 12.sp, color = statusColor, fontWeight = FontWeight.Medium)
        }
    }
}
