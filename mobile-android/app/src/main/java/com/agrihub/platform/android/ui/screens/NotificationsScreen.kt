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
fun NotificationsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold) },
                actions = { TextButton(onClick = { }) { Text("Mark All Read", color = ForestGreen) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhite)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(OffWhite).padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item { Text("Today", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = SlateGrey, modifier = Modifier.padding(bottom = 4.dp)) }
            item { NotificationItem("Weather Alert", "Rain expected tomorrow. Delay fertilizer application.", "10:30 AM", false, WarningOrange) }
            item { NotificationItem("Crop Alert", "Field 3 maize ready for top dressing.", "09:15 AM", true, ForestGreen) }
            item { NotificationItem("Market Update", "Soybean prices increased by 8% in your region.", "08:00 AM", true, InfoBlue) }
            item { Spacer(Modifier.height(8.dp)) }
            item { Text("Yesterday", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = SlateGrey, modifier = Modifier.padding(bottom = 4.dp)) }
            item { NotificationItem("Vaccination Due", "Three cattle vaccinations due this week.", "4:45 PM", true, WarningOrange) }
            item { NotificationItem("Payment Received", "MWK 850,000 received from Maize sale.", "2:30 PM", true, ForestGreen) }
        }
    }
}

@Composable
fun NotificationItem(title: String, message: String, time: String, isRead: Boolean, color: androidx.compose.ui.graphics.Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = if (isRead) SurfaceWhite else SurfaceWhite),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(Modifier.padding(12.dp)) {
            if (!isRead) {
                Box(Modifier.width(8.dp).height(8.dp).background(color, RoundedCornerShape(50)))
                Spacer(Modifier.width(8.dp))
            } else {
                Spacer(Modifier.width(16.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Charcoal)
                Text(message, fontSize = 13.sp, color = SlateGrey)
                Text(time, fontSize = 11.sp, color = SlateGrey)
            }
        }
    }
}
