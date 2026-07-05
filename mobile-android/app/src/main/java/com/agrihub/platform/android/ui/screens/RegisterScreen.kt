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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.agrihub.platform.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var currentStep by remember { mutableStateOf(1) }
    val totalSteps = 4
    
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf("Malawi") }
    var selectedRegion by remember { mutableStateOf("") }
    var selectedDistrict by remember { mutableStateOf("") }
    var farmerType by remember { mutableStateOf("Individual Farmer") }
    var verificationCode by remember { mutableStateOf("") }
    var acceptedTerms by remember { mutableStateOf(false) }
    var acceptedPrivacy by remember { mutableStateOf(false) }
    
    val farmerTypes = listOf("Individual Farmer", "Cooperative", "Agribusiness", "Extension Officer", "NGO", "Government", "Buyer", "Supplier")
    val countries = listOf("Malawi", "Zambia", "Tanzania", "Kenya", "Rwanda")
    val regions = when(selectedCountry) {
        "Malawi" -> listOf("Northern", "Central", "Southern")
        "Zambia" -> listOf("Central", "Copperbelt", "Eastern", "Luapula", "Lusaka", "Northern", "Southern", "Western")
        else -> listOf("Region 1", "Region 2")
    }
    val districts = when(selectedRegion) {
        "Northern" -> listOf("Chitipa", "Karonga", "Rumphi", "Mzimba", "Nkhata Bay", "Likoma")
        "Central" -> listOf("Kasungu", "Nkhotakota", "Ntchisi", "Dowa", "Mchinji", "Lilongwe", "Dedza", "Ntcheu", "Salima")
        "Southern" -> listOf("Mangochi", "Machinga", "Zomba", "Chiradzulu", "Blantyre", "Mwanza", "Thyolo", "Mulanje", "Phalombe", "Chikwawa", "Nsanje", "Balaka", "Neno")
        else -> listOf("District 1", "District 2")
    }
    
    val farmingActivities = listOf("Maize", "Tobacco", "Rice", "Groundnuts", "Soybeans", "Vegetables", "Livestock", "Coffee", "Tea", "Cotton", "Cassava")
    var selectedActivities by remember { mutableStateOf(setOf<String>()) }
    
    val passwordStrength = when {
        password.length < 6 -> Pair("Weak", ErrorRed, 0.25f)
        password.length < 8 -> Pair("Fair", WarningOrange, 0.50f)
        password.any { it.isDigit() } && password.any { it.isUpperCase() } && password.any { !it.isLetterOrDigit() } -> Pair("Strong", ForestGreen, 1.0f)
        else -> Pair("Medium", WarningOrange, 0.75f)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account", fontWeight = FontWeight.Bold) },
                navigationIcon = { TextButton(onClick = { if (currentStep > 1) currentStep-- else navController.popBackStack() }) { Text("< Back", color = ForestGreen) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(OffWhite).padding(padding).padding(24.dp).verticalScroll(rememberScrollState())
        ) {
            // Progress Indicator
            Text("Step $currentStep of $totalSteps", fontSize = 13.sp, color = SlateGrey, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { currentStep.toFloat() / totalSteps.toFloat() },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = ForestGreen,
                trackColor = ForestGreen.copy(alpha = 0.15f),
                strokeCap = StrokeCap.Round
            )
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                for (i in 1..totalSteps) {
                    Box(Modifier.size(8.dp).background(if (i <= currentStep) ForestGreen else DisabledGrey, RoundedCornerShape(4.dp)))
                }
            }
            Spacer(Modifier.height(24.dp))
            
            when (currentStep) {
                1 -> {
                    Text("Personal Details", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Charcoal)
                    Text("Tell us who you are", fontSize = 13.sp, color = SlateGrey)
                    Spacer(Modifier.height(20.dp))
                    
                    OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), singleLine = true)
                    Spacer(Modifier.height(12.dp))
                    
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), singleLine = true)
                    Spacer(Modifier.height(12.dp))
                    
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true)
                    Spacer(Modifier.height(20.dp))
                    
                    Text("Register As", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Charcoal)
                    Spacer(Modifier.height(8.dp))
                    farmerTypes.take(4).forEach { type ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = farmerType == type, onClick = { farmerType = type }, colors = RadioButtonDefaults.colors(selectedColor = ForestGreen))
                            Text(type, fontSize = 14.sp, color = Charcoal)
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { currentStep = 2 }, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = ForestGreen), enabled = fullName.isNotBlank() && phone.isNotBlank()) {
                        Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                
                2 -> {
                    Text("Security", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Charcoal)
                    Text("Create a strong password", fontSize = 13.sp, color = SlateGrey)
                    Spacer(Modifier.height(20.dp))
                    
                    OutlinedTextField(
                        value = password, onValueChange = { password = it },
                        label = { Text("Password") }, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = { TextButton(onClick = { passwordVisible = !passwordVisible }) { Text(if (passwordVisible) "Hide" else "Show", color = SlateGrey, fontSize = 12.sp) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), singleLine = true
                    )
                    
                    if (password.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(progress = { passwordStrength.third }, modifier = Modifier.fillMaxWidth().height(4.dp), color = passwordStrength.second, trackColor = passwordStrength.second.copy(alpha = 0.15f), strokeCap = StrokeCap.Round)
                        Text(passwordStrength.first, fontSize = 12.sp, color = passwordStrength.second)
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = confirmPassword, onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") }, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), singleLine = true,
                        isError = confirmPassword.isNotEmpty() && password != confirmPassword
                    )
                    if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                        Text("Passwords do not match", fontSize = 12.sp, color = ErrorRed)
                    }
                    
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = { currentStep = 3 }, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = ForestGreen), enabled = password.length >= 6 && password == confirmPassword) {
                        Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                
                3 -> {
                    Text("Location & Preferences", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Charcoal)
                    Text("Where is your farm located?", fontSize = 13.sp, color = SlateGrey)
                    Spacer(Modifier.height(20.dp))
                    
                    Text("Country", fontSize = 13.sp, color = SlateGrey, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(4.dp))
                    countries.forEach { country ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedCountry == country, onClick = { selectedCountry = country; selectedRegion = ""; selectedDistrict = "" }, colors = RadioButtonDefaults.colors(selectedColor = ForestGreen))
                            Text(country, fontSize = 14.sp, color = Charcoal)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    
                    Text("Region", fontSize = 13.sp, color = SlateGrey, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(4.dp))
                    regions.forEach { region ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedRegion == region, onClick = { selectedRegion = region; selectedDistrict = "" }, colors = RadioButtonDefaults.colors(selectedColor = ForestGreen))
                            Text(region, fontSize = 14.sp, color = Charcoal)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    
                    if (selectedRegion.isNotEmpty()) {
                        Text("District", fontSize = 13.sp, color = SlateGrey, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(4.dp))
                        districts.forEach { district ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = selectedDistrict == district, onClick = { selectedDistrict = district }, colors = RadioButtonDefaults.colors(selectedColor = ForestGreen))
                                Text(district, fontSize = 14.sp, color = Charcoal)
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { currentStep = 4 }, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = ForestGreen), enabled = selectedCountry.isNotEmpty() && selectedRegion.isNotEmpty() && selectedDistrict.isNotEmpty()) {
                        Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                
                4 -> {
                    Text("Farming Activities", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Charcoal)
                    Text("What do you farm? Select all that apply.", fontSize = 13.sp, color = SlateGrey)
                    Spacer(Modifier.height(16.dp))
                    
                    farmingActivities.chunked(3).forEach { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { activity ->
                                FilterChip(
                                    selected = activity in selectedActivities,
                                    onClick = {
                                        selectedActivities = if (activity in selectedActivities) selectedActivities - activity else selectedActivities + activity
                                    },
                                    label = { Text(activity, fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ForestGreen.copy(alpha = 0.15f), selectedLabelColor = ForestGreen)
                                )
                            }
                            repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                    
                    Spacer(Modifier.height(20.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = acceptedTerms, onCheckedChange = { acceptedTerms = it }, colors = CheckboxDefaults.colors(checkedColor = ForestGreen))
                        Text("I accept the ", fontSize = 13.sp, color = SlateGrey)
                        Text("Terms of Service", fontSize = 13.sp, color = ForestGreen, fontWeight = FontWeight.Medium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = acceptedPrivacy, onCheckedChange = { acceptedPrivacy = it }, colors = CheckboxDefaults.colors(checkedColor = ForestGreen))
                        Text("I accept the ", fontSize = 13.sp, color = SlateGrey)
                        Text("Privacy Policy", fontSize = 13.sp, color = ForestGreen, fontWeight = FontWeight.Medium)
                    }
                    
                    Spacer(Modifier.height(20.dp))
                    
                    Button(
                        onClick = { navController.navigate("verify-phone") },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        enabled = acceptedTerms && acceptedPrivacy && selectedActivities.isNotEmpty()
                    ) {
                        Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
