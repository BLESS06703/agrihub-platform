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
fun FinanceScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(OffWhite),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Text("Finance", style = MaterialTheme.typography.headlineMedium, color = ModuleFinance) }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = ForestGreen)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Net Profit (June)", style = MaterialTheme.typography.titleSmall, color = TextOnPrimary)
                    Text("MWK 2,800,000", style = MaterialTheme.typography.headlineLarge, color = TextOnPrimary)
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceWhite)) {
                Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column { Text("Income", color = SlateGrey); Text("MWK 4.2M", color = ForestGreen) }
                    Column { Text("Expenses", color = SlateGrey); Text("MWK 1.4M", color = ErrorRed) }
                }
            }
        }
    }
}
