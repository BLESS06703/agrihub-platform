package com.agrihub.platform.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.agrihub.platform.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(OffWhite),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TopAppBar(
                title = { Text("AgriHub", fontWeight = FontWeight.Bold, color = ForestGreen) },
                actions = {
                    TextButton(onClick = { }) {
                        Text("3", color = ForestGreen, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhite)
            )
        }
        
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Good Morning, Scott", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Charcoal)
                    Text("Blantyre   24C   Rain tomorrow", fontSize = 13.sp, color = SlateGrey)
                }
            }
        }
        
        item {
            Card(colors = CardDefaults.cardColors(containerColor = ForestGreen), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Text("Monthly Profit", color = TextOnPrimary.copy(alpha = 0.8f), fontSize = 12.sp)
                    Text("MWK 2,800,000", color = TextOnPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        item { SectionHeader("Alerts") }
        
        item {
            AlertCard("Rain expected tomorrow. Delay fertilizer application.", "MEDIUM", {})
        }
        item {
            AlertCard("Field 3 maize ready for top dressing.", "MEDIUM", {})
        }
        item {
            AlertCard("Three cattle vaccinations due this week.", "HIGH", { navController.navigate("livestock") })
        }
        
        item { SectionHeader("Farm Overview") }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard("Active Crops", "5", ForestGreen, Modifier.weight(1f))
                MetricCard("Livestock", "23", ModuleLivestock, Modifier.weight(1f))
                MetricCard("Pending Tasks", "8", WarningOrange, Modifier.weight(1f))
            }
        }
        
        item { SectionHeader("AI Insights") }
        item {
            InsightCard("Two tomato plants show signs of early blight. Recommended treatment available.", 0.89, ErrorRed, { navController.navigate("ai-assistant") })
        }
        item {
            InsightCard("Estimated monthly profit: MWK 2.8M based on current market prices.", 0.92, ForestGreen, {})
        }
        item {
            InsightCard("Optimal planting window for maize starts in 5 days based on forecast.", 0.85, ModuleAI, {})
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Charcoal, modifier = Modifier.padding(top = 8.dp))
}

@Composable
fun AlertCard(message: String, priority: String, action: () -> Unit) {
    val borderColor = when(priority) {
        "HIGH" -> ErrorRed
        else -> WarningOrange
    }
    
    Card(colors = CardDefaults.cardColors(containerColor = SurfaceWhite), shape = RoundedCornerShape(12.dp)) {
        Row(Modifier.padding(16.dp)) {
            Box(Modifier.width(4.dp).height(50.dp).background(borderColor, RoundedCornerShape(2.dp)))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(message, color = Charcoal, fontSize = 14.sp)
                if (priority == "HIGH") {
                    TextButton(onClick = action, contentPadding = PaddingValues(0.dp)) {
                        Text("Take Action", color = ForestGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Card(colors = CardDefaults.cardColors(containerColor = SurfaceWhite), shape = RoundedCornerShape(12.dp), modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 12.sp, color = SlateGrey)
        }
    }
}

@Composable
fun InsightCard(message: String, confidence: Double, color: androidx.compose.ui.graphics.Color, action: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = SurfaceWhite), shape = RoundedCornerShape(12.dp)) {
        Row(Modifier.padding(16.dp)) {
            Box(Modifier.width(4.dp).height(60.dp).background(color, RoundedCornerShape(2.dp)))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(message, color = Charcoal, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                Text("Confidence: ${(confidence * 100).toInt()}%", color = SlateGrey, fontSize = 12.sp)
            }
        }
    }
}
