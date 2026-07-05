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
import androidx.navigation.NavController
import com.agrihub.platform.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmListScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farms", fontWeight = FontWeight.Bold) },
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
            item { FarmCard("Mzuzu Coffee Farm", "5 fields", "12.5 ha", "ACTIVE", { navController.navigate("farm/farm-1") }) }
            item { FarmCard("Lilongwe Vegetable Farm", "3 fields", "8.2 ha", "ACTIVE", { navController.navigate("farm/farm-2") }) }
            item { FarmCard("Blantyre Mixed Farm", "8 fields", "25.0 ha", "ACTIVE", { navController.navigate("farm/farm-3") }) }
        }
    }
}

@Composable
fun FarmCard(name: String, fields: String, area: String, status: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Charcoal)
                Text("$fields   $area", fontSize = 13.sp, color = SlateGrey)
            }
            Box {
                Text("[o]", color = ForestGreen, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun FarmDetailScreen(farmId: String, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farm Detail", fontWeight = FontWeight.Bold) },
                navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text("< Back", color = ForestGreen) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhite)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(OffWhite).padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("$farmId - Detail View", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
        }
    }
}
