package com.agrihub.platform.android.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.agrihub.platform.android.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(navController: NavController) {
    var progress by remember { mutableStateOf(0f) }
    var statusText by remember { mutableStateOf("Loading your farms...") }
    
    LaunchedEffect(Unit) {
        delay(600)
        statusText = "Syncing weather data..."
        delay(600)
        statusText = "Checking market prices..."
        delay(600)
        statusText = "Almost ready..."
        delay(400)
        navController.navigate("dashboard") { popUpTo("login") { inclusive = true } }
    }
    
    Column(
        modifier = Modifier.fillMaxSize().background(OffWhite).padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("AGRIHUB", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = ForestGreen, letterSpacing = 4.sp)
        Spacer(Modifier.height(8.dp))
        Text("Welcome back, Scott", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Charcoal)
        Spacer(Modifier.height(32.dp))
        
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = ForestGreen,
            trackColor = ForestGreen.copy(alpha = 0.1f),
            strokeCap = StrokeCap.Round
        )
        
        Spacer(Modifier.height(16.dp))
        Text(statusText, fontSize = 14.sp, color = SlateGrey, textAlign = TextAlign.Center)
        Spacer(Modifier.height(4.dp))
        Text("Please wait...", fontSize = 12.sp, color = SlateGrey)
    }
}
