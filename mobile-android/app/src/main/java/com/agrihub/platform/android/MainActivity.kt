package com.agrihub.platform.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.agrihub.platform.android.ui.screens.*
import com.agrihub.platform.android.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgriHubTheme {
                AgriHubNavigation()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgriHubNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val showBottomBar = currentRoute in listOf("dashboard", "farms", "reports", "profile")
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = SurfaceWhite) {
                    NavigationBarItem(
                        selected = currentRoute == "dashboard",
                        onClick = { navController.navigate("dashboard") { popUpTo("dashboard") { inclusive = true } } },
                        icon = { Text("H", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (currentRoute == "dashboard") ForestGreen else SlateGrey) },
                        label = { Text("Home", fontSize = 11.sp, color = if (currentRoute == "dashboard") ForestGreen else SlateGrey) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "farms",
                        onClick = { navController.navigate("farms") { popUpTo("farms") { inclusive = true } } },
                        icon = { Text("F", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (currentRoute == "farms") ForestGreen else SlateGrey) },
                        label = { Text("Farms", fontSize = 11.sp, color = if (currentRoute == "farms") ForestGreen else SlateGrey) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate("quick-action") },
                        icon = { Box(Modifier.size(40.dp).background(ForestGreen, RoundedCornerShape(20.dp)), contentAlignment = androidx.compose.ui.Alignment.Center) { Text("+", color = TextOnPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) } },
                        label = { Text("Add", fontSize = 11.sp, color = ForestGreen) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "reports",
                        onClick = { navController.navigate("reports") { popUpTo("reports") { inclusive = true } } },
                        icon = { Text("R", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (currentRoute == "reports") ForestGreen else SlateGrey) },
                        label = { Text("Reports", fontSize = 11.sp, color = if (currentRoute == "reports") ForestGreen else SlateGrey) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "profile",
                        onClick = { navController.navigate("profile") { popUpTo("profile") { inclusive = true } } },
                        icon = { Text("P", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (currentRoute == "profile") ForestGreen else SlateGrey) },
                        label = { Text("Profile", fontSize = 11.sp, color = if (currentRoute == "profile") ForestGreen else SlateGrey) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController, startDestination = "dashboard", modifier = Modifier.padding(padding)) {
            composable("dashboard") { DashboardScreen(navController) }
            composable("farms") { FarmListScreen(navController) }
            composable("reports") { ReportsScreen() }
            composable("profile") { ProfileScreen(navController) }
            composable("quick-action") { QuickActionScreen(navController) }
            composable("login") { LoginScreen(navController) }
            composable("welcome") { WelcomeScreen(navController) }
            composable("marketplace") { MarketplaceScreen() }
            composable("finance") { FinanceScreen() }
            composable("ai-assistant") { AIAssistantScreen() }
            composable("livestock") { LivestockScreen() }
            composable("inventory") { InventoryScreen() }
            composable("warehouse") { WarehouseScreen() }
            composable("notifications") { NotificationsScreen() }
            composable("farm/{farmId}", arguments = listOf(navArgument("farmId") { type = NavType.StringType })) { entry ->
                FarmDetailScreen(entry.arguments?.getString("farmId") ?: "", navController)
            }
        }
    }
}
