package com.agrihub.platform.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.agrihub.platform.android.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneVerificationScreen(navController: NavController) {
    var code by remember { mutableStateOf("") }
    var resendTimer by remember { mutableStateOf(40) }
    var isVerified by remember { mutableStateOf(false) }
    
    LaunchedEffect(resendTimer) {
        if (resendTimer > 0) {
            delay(1000)
            resendTimer--
        }
    }
    
    Scaffold(
        topBar = { TopAppBar(title = { Text("Verify Phone", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhite)) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(OffWhite).padding(padding).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Verification Code", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Charcoal)
            Spacer(Modifier.height(8.dp))
            Text("We sent a 6-digit code to your phone", fontSize = 14.sp, color = SlateGrey, textAlign = TextAlign.Center)
            Spacer(Modifier.height(32.dp))
            
            OutlinedTextField(
                value = code, onValueChange = { if (it.length <= 6) code = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 24.sp, textAlign = TextAlign.Center, letterSpacing = 8.sp)
            )
            
            Spacer(Modifier.height(24.dp))
            
            Button(
                onClick = { isVerified = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                enabled = code.length == 6
            ) {
                Text("Verify", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            
            Spacer(Modifier.height(16.dp))
            
            if (resendTimer > 0) {
                Text("Resend code in $resendTimer seconds", fontSize = 13.sp, color = SlateGrey)
            } else {
                TextButton(onClick = { resendTimer = 40 }) {
                    Text("Resend Code", color = ForestGreen, fontWeight = FontWeight.SemiBold)
                }
            }
            
            if (isVerified) {
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { navController.navigate("onboarding") { popUpTo("login") { inclusive = true } } },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text("Continue to Farm Setup", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
