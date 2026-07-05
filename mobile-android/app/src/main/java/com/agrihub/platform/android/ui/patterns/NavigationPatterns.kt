package com.agrihub.platform.android.ui.patterns

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.agrihub.platform.android.ui.theme.*

data class BottomNavItem(val label: String, val route: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem("Home", "dashboard", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem("Farms", "farms", Icons.Filled.Grass, Icons.Outlined.Grass),
    BottomNavItem("Add", "quick-action", Icons.Filled.AddCircle, Icons.Outlined.AddCircle),
    BottomNavItem("Reports", "reports", Icons.Filled.BarChart, Icons.Outlined.BarChart),
    BottomNavItem("Profile", "profile", Icons.Filled.Person, Icons.Outlined.Person)
)

data class QuickAction(val label: String, val route: String)

val quickActions = listOf(
    QuickAction("Record Planting", "farms/plant"),
    QuickAction("Record Harvest", "farms/harvest"),
    QuickAction("Add Animal", "livestock/add"),
    QuickAction("Add Expense", "finance/expense"),
    QuickAction("Create Listing", "marketplace/sell")
)

data class SidebarGroup(val title: String, val items: List<SidebarItem>)
data class SidebarItem(val label: String, val route: String)

val adminSidebar = listOf(
    SidebarGroup("Overview", listOf(SidebarItem("Dashboard", "/admin"))),
    SidebarGroup("Agriculture", listOf(
        SidebarItem("Farms", "/admin/farms"),
        SidebarItem("Fields", "/admin/fields"),
        SidebarItem("Crops", "/admin/crops"),
        SidebarItem("Harvests", "/admin/harvests")
    )),
    SidebarGroup("Operations", listOf(
        SidebarItem("Livestock", "/admin/livestock"),
        SidebarItem("Inventory", "/admin/inventory"),
        SidebarItem("Warehouse", "/admin/warehouse"),
        SidebarItem("Marketplace", "/admin/marketplace")
    )),
    SidebarGroup("Finance", listOf(
        SidebarItem("Income", "/admin/income"),
        SidebarItem("Expenses", "/admin/expenses"),
        SidebarItem("Budgets", "/admin/budgets"),
        SidebarItem("Loans", "/admin/loans")
    )),
    SidebarGroup("Intelligence", listOf(
        SidebarItem("AI Assistant", "/admin/ai"),
        SidebarItem("Reports", "/admin/reports"),
        SidebarItem("Analytics", "/admin/analytics")
    )),
    SidebarGroup("System", listOf(
        SidebarItem("Tenants", "/admin/tenants"),
        SidebarItem("Users", "/admin/users"),
        SidebarItem("Settings", "/admin/settings"),
        SidebarItem("Audit Log", "/admin/audit")
    ))
)
