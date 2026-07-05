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
fun ReportsScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Reports", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhite)) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(OffWhite).padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Text("Yield Report", Modifier.padding(16.dp).fillMaxWidth(), color = Charcoal) }
            item { Text("Financial Report", Modifier.padding(16.dp).fillMaxWidth(), color = Charcoal) }
            item { Text("Livestock Report", Modifier.padding(16.dp).fillMaxWidth(), color = Charcoal) }
        }
    }
}

@Composable
fun ProfileScreen(navController: androidx.navigation.NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Profile", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhite)) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(OffWhite).padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Text("Scott Manda", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
            item { Text("scott@farm.com", color = SlateGrey) }
            item { Divider(Modifier.padding(vertical = 8.dp)) }
            item { Text("Language: English", Modifier.padding(8.dp)) }
            item { Text("Notifications", Modifier.padding(8.dp)) }
            item { Text("Security", Modifier.padding(8.dp)) }
            item { Text("Help & Support", Modifier.padding(8.dp)) }
            item { Spacer(Modifier.height(24.dp)) }
            item { Button(onClick = { navController.navigate("login") }, colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)) { Text("Logout") } }
        }
    }
}

@Composable
fun QuickActionScreen(navController: androidx.navigation.NavController) {
    Column(Modifier.fillMaxSize().background(OffWhite).padding(32.dp), verticalArrangement = Arrangement.Center) {
        listOf("Record Planting", "Record Harvest", "Add Animal", "Add Expense", "Create Listing").forEach { action ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite)) {
                Text(action, Modifier.padding(16.dp), color = Charcoal)
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = SlateGrey)) { Text("Cancel") }
    }
}

@Composable
fun MarketplaceScreen() {
    Scaffold(topBar = { TopAppBar(title = { Text("Marketplace") }) }) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            item { Text("Marketplace listings", Modifier.padding(16.dp)) }
        }
    }
}

@Composable
fun FinanceScreen() {
    Scaffold(topBar = { TopAppBar(title = { Text("Finance") }) }) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            item { Text("Finance overview", Modifier.padding(16.dp)) }
        }
    }
}

@Composable
fun AIAssistantScreen() {
    Scaffold(topBar = { TopAppBar(title = { Text("AI Assistant") }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("AgriHub AI", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("How can I help today?")
            Spacer(Modifier.height(24.dp))
            listOf("Diagnose Disease", "Weather Advice", "Market Prices", "Fertilizer Advice", "Yield Prediction").forEach { action ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(8.dp)) {
                    Text(action, Modifier.padding(16.dp))
                }
            }
        }
    }
}
