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
fun LivestockScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Livestock", fontWeight = FontWeight.Bold) },
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
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = true, onClick = { }, label = { Text("All") }, modifier = Modifier.weight(1f))
                    FilterChip(selected = false, onClick = { }, label = { Text("Cattle") }, modifier = Modifier.weight(1f))
                    FilterChip(selected = false, onClick = { }, label = { Text("Pigs") }, modifier = Modifier.weight(1f))
                    FilterChip(selected = false, onClick = { }, label = { Text("Poultry") }, modifier = Modifier.weight(1f))
                }
            }
            item { Spacer(Modifier.height(4.dp)) }
            item { AnimalCard("COW-042", "Cattle", "Friesian", "ALIVE", "420 kg", "Pen A", listOf("Vaccination Due: FMD")) }
            item { AnimalCard("PIG-015", "Pig", "Large White", "ALIVE", "85 kg", "Pen C", listOf("Farrowing expected: July 15")) }
            item { AnimalCard("COW-018", "Cattle", "Jersey", "SICK", "380 kg", "Pen B", listOf("Treatment: Antibiotics", "Follow-up: July 7")) }
            item { AnimalCard("CHK-230", "Chicken", "Kuroiler", "ALIVE", "-", "Coop 2", listOf("Egg production: Above target")) }
        }
    }
}

@Composable
fun AnimalCard(tagId: String, species: String, breed: String, status: String, weight: String, location: String, alerts: List<String>) {
    val statusColor = when(status) { "ALIVE" -> ForestGreen; "SICK" -> ErrorRed; else -> SlateGrey }
    
    Card(colors = CardDefaults.cardColors(containerColor = SurfaceWhite), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("$tagId - $breed", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Charcoal)
                Text(status, fontSize = 12.sp, color = statusColor, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(4.dp))
            Text("$species    $weight    $location", fontSize = 13.sp, color = SlateGrey)
            if (alerts.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                alerts.forEach { alert ->
                    Row(Modifier.padding(vertical = 2.dp)) {
                        Text("[!]", color = WarningOrange, fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(alert, fontSize = 12.sp, color = WarningOrange)
                    }
                }
            }
        }
    }
}
