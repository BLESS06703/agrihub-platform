package com.agrihub.platform.android.ui.screens.admin

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
fun AdminDashboardScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Admin", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhite)) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(OffWhite).padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminStatCard("Tenants", "1,247", ForestGreen, Modifier.weight(1f))
                    AdminStatCard("Farmers", "8,542", SuccessGreen, Modifier.weight(1f))
                }
            }
            item { AdminNavCard("Tenants", "Manage platform organizations", ModuleFarm) }
            item { AdminNavCard("Users", "User accounts and roles", InfoBlue) }
            item { AdminNavCard("Reports", "Platform analytics", ModuleReports) }
            item { AdminNavCard("Settings", "Platform configuration", SlateGrey) }
        }
    }
}

@Composable
fun AdminStatCard(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Card(modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite)) {
        Column(Modifier.padding(16.dp)) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 12.sp, color = SlateGrey)
        }
    }
}

@Composable
fun AdminNavCard(title: String, description: String, color: androidx.compose.ui.graphics.Color) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite)) {
        Row(Modifier.padding(16.dp).fillMaxWidth()) {
            Box(Modifier.width(4.dp).height(40.dp).background(color, RoundedCornerShape(2.dp)))
            Spacer(Modifier.width(12.dp))
            Column { Text(title, fontWeight = FontWeight.SemiBold, color = Charcoal); Text(description, fontSize = 13.sp, color = SlateGrey) }
        }
    }
}
