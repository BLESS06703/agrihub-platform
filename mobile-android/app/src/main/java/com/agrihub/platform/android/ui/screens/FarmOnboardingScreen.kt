package com.agrihub.platform.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.agrihub.platform.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmOnboardingScreen(navController: NavController) {
    var farmName by remember { mutableStateOf("") }
    var farmSize by remember { mutableStateOf("") }
    var primaryActivity by remember { mutableStateOf("Crop Farming") }
    
    Scaffold(
        topBar = { TopAppBar(title = { Text("Farm Setup", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhite)) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(OffWhite).padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to AgriHub", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
            Text("Let's set up your first farm", fontSize = 14.sp, color = SlateGrey)
            Spacer(Modifier.height(32.dp))
            
            OutlinedTextField(value = farmName, onValueChange = { farmName = it }, label = { Text("Farm Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), singleLine = true)
            Spacer(Modifier.height(12.dp))
            
            OutlinedTextField(value = farmSize, onValueChange = { farmSize = it }, label = { Text("Farm Size (hectares)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), singleLine = true, suffix = { Text("ha") })
            Spacer(Modifier.height(16.dp))
            
            Text("Primary Activity", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Charcoal)
            listOf("Crop Farming", "Livestock", "Mixed Farming").forEach { activity ->
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = primaryActivity == activity, onClick = { primaryActivity = activity }, colors = RadioButtonDefaults.colors(selectedColor = ForestGreen))
                    Text(activity, fontSize = 14.sp, color = Charcoal)
                }
            }
            
            Spacer(Modifier.height(32.dp))
            
            Button(
                onClick = { navController.navigate("dashboard") { popUpTo("login") { inclusive = true } } },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                enabled = farmName.isNotBlank()
            ) {
                Text("Finish Setup", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { navController.navigate("dashboard") { popUpTo("login") { inclusive = true } } }) {
                Text("Skip for now", color = SlateGrey)
            }
        }
    }
}
