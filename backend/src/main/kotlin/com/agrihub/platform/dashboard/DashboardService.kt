package com.agrihub.platform.dashboard

import org.slf4j.LoggerFactory
import java.util.UUID

class DashboardService {
    private val logger = LoggerFactory.getLogger(DashboardService::class.java)
    
    data class DashboardData(
        val alerts: List<Alert>,
        val quickActions: List<QuickAction>,
        val metrics: Metrics,
        val aiInsights: List<AIInsight>
    )
    
    data class Alert(
        val type: String,
        val priority: String,
        val message: String,
        val action: String?
    )
    
    data class QuickAction(
        val id: String,
        val label: String,
        val icon: String,
        val route: String
    )
    
    data class Metrics(
        val activeCrops: Int,
        val livestockCount: Int,
        val pendingTasks: Int,
        val monthlyProfit: Double,
        val currency: String = "MWK"
    )
    
    data class AIInsight(
        val category: String,
        val message: String,
        val confidence: Double,
        val actionLabel: String?
    )
    
    suspend fun getDashboard(tenantId: UUID, userId: UUID): DashboardData {
        logger.info("Generating dashboard for tenant={}, user={}", tenantId, userId)
        
        return DashboardData(
            alerts = listOf(
                Alert("WEATHER", "HIGH", "Rain expected tomorrow. Delay fertilizer application.", "View forecast"),
                Alert("CROP", "MEDIUM", "Field 3 maize is ready for top dressing.", "View field"),
                Alert("LIVESTOCK", "HIGH", "Three cattle vaccinations are due this week.", "Schedule now"),
                Alert("MARKET", "INFO", "Soybean prices have increased by 8% in your region.", "View prices")
            ),
            quickActions = listOf(
                QuickAction("plant", "Record Planting", "agriculture", "/farms/plant"),
                QuickAction("harvest", "Record Harvest", "agriculture", "/farms/harvest"),
                QuickAction("expense", "Add Expense", "finance", "/finance/expenses"),
                QuickAction("market", "Sell Produce", "marketplace", "/marketplace/sell")
            ),
            metrics = Metrics(
                activeCrops = 5,
                livestockCount = 23,
                pendingTasks = 8,
                monthlyProfit = 2800000.00
            ),
            aiInsights = listOf(
                AIInsight("DISEASE", "Two tomato plants show signs of early blight. Recommended treatment available.", 0.89, "View treatment"),
                AIInsight("YIELD", "Estimated monthly profit: MWK 2.8 million based on current market prices.", 0.92, null),
                AIInsight("WEATHER", "Optimal planting window for maize starts in 5 days based on forecast.", 0.85, "Plan planting")
            )
        )
    }
}
