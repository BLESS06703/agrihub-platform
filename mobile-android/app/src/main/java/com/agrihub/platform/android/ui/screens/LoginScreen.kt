package com.agrihub.platform.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.agrihub.platform.android.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(navController: NavController) {
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberDevice by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedCountry by remember { mutableStateOf("Malawi") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhite)
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        
        // Brand Header
        Text("AGRIHUB", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = ForestGreen, letterSpacing = 4.sp)
        Text("Smart Agriculture Platform", fontSize = 14.sp, color = SlateGrey, letterSpacing = 1.sp)
        
        Spacer(Modifier.height(40.dp))
        
        Text("Welcome Back", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Charcoal)
        Text("Sign in to your account", fontSize = 14.sp, color = SlateGrey)
        
        Spacer(Modifier.height(32.dp))
        
        // Email/Phone Field
        OutlinedTextField(
            value = identifier,
            onValueChange = { identifier = it; errorMessage = null },
            label = { Text("Email or Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ForestGreen,
                focusedLabelColor = ForestGreen,
                cursorColor = ForestGreen
            )
        )
        
        Spacer(Modifier.height(16.dp))
        
        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(if (passwordVisible) "Hide" else "Show", fontSize = 13.sp, color = SlateGrey)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ForestGreen,
                focusedLabelColor = ForestGreen,
                cursorColor = ForestGreen
            )
        )
        
        Spacer(Modifier.height(8.dp))
        
        // Error Message
        if (errorMessage != null) {
            Text(errorMessage!!, fontSize = 13.sp, color = ErrorRed, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
        }
        
        // Remember Device + Forgot Password
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberDevice,
                    onCheckedChange = { rememberDevice = it },
                    colors = CheckboxDefaults.colors(checkedColor = ForestGreen)
                )
                Text("Remember this device", fontSize = 13.sp, color = SlateGrey)
            }
            TextButton(onClick = { }) {
                Text("Forgot Password?", fontSize = 13.sp, color = ForestGreen, fontWeight = FontWeight.Medium)
            }
        }
        
        Spacer(Modifier.height(24.dp))
        
        // Sign In Button
        Button(
            onClick = {
                isLoading = true
                errorMessage = null
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen, disabledContainerColor = ForestGreen.copy(alpha = 0.6f)),
            enabled = identifier.isNotBlank() && password.isNotBlank() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = TextOnPrimary, strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text("Signing in...", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            } else {
                Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        
        Spacer(Modifier.height(24.dp))
        
        // Divider
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Divider(Modifier.weight(1f), color = DisabledGrey)
            Text("  or  ", fontSize = 13.sp, color = SlateGrey)
            Divider(Modifier.weight(1f), color = DisabledGrey)
        }
        
        Spacer(Modifier.height(16.dp))
        
        // Alternative Sign In
        OutlinedButton(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Charcoal)
        ) {
            Text("G", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = InfoBlue)
            Spacer(Modifier.width(8.dp))
            Text("Sign in with Google", fontSize = 14.sp, color = Charcoal)
        }
        
        Spacer(Modifier.height(8.dp))
        
        OutlinedButton(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Charcoal)
        ) {
            Text("OTP", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
            Spacer(Modifier.width(8.dp))
            Text("Sign in with Phone OTP", fontSize = 14.sp, color = Charcoal)
        }
        
        Spacer(Modifier.height(24.dp))
        
        // Register link
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Don't have an account?", fontSize = 14.sp, color = SlateGrey)
            TextButton(onClick = { navController.navigate("register") }) {
                Text("Register", fontSize = 14.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        // Country Selector
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text("MW", fontSize = 13.sp, color = SlateGrey, fontWeight = FontWeight.Medium)
            Spacer(Modifier.width(4.dp))
            Text(selectedCountry, fontSize = 13.sp, color = SlateGrey)
        }
        
        Spacer(Modifier.height(16.dp))
        
        // Agriculture Snapshot Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ForestGreen.copy(alpha = 0.05f))
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Today in Malawi", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    Text("Rain expected in Southern Region", fontSize = 12.sp, color = Charcoal)
                }
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth()) {
                    Text("Maize Market Price: MWK 1,250/kg", fontSize = 12.sp, color = Charcoal)
                }
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth()) {
                    Text("Tip: Rotate crops to improve soil health.", fontSize = 12.sp, color = SlateGrey)
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        // Security Notice
        Text(
            "Protected with enterprise-grade security",
            fontSize = 11.sp,
            color = SlateGrey,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(Modifier.height(24.dp))
    }
}
