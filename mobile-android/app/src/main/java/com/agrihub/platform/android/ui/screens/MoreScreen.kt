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
fun MoreScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(OffWhite),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item { Text("More", style = MaterialTheme.typography.headlineMedium, color = Charcoal) }
        item { MenuRow("Livestock", ModuleLivestock) }
        item { MenuRow("Inventory", ModuleInventory) }
        item { MenuRow("Warehouse", ModuleWarehouse) }
        item { MenuRow("Reports", ModuleReports) }
        item { MenuRow("AI Assistant", ModuleAI) }
        item { Divider(Modifier.padding(vertical = 8.dp)) }
        item { MenuRow("Settings", SlateGrey) }
        item { MenuRow("Help & Support", SlateGrey) }
        item { MenuRow("Logout", ErrorRed) }
    }
}

@Composable
fun MenuRow(label: String, color: androidx.compose.ui.graphics.Color) {
    Card(colors = CardDefaults.cardColors(containerColor = SurfaceWhite)) {
        Text(label, Modifier.padding(16.dp).fillMaxWidth(), style = MaterialTheme.typography.titleMedium, color = color)
    }
}
