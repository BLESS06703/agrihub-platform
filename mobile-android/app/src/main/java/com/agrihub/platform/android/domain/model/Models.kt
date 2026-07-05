package com.agrihub.platform.android.domain.model

data class Farm(val id: String, val name: String, val areaHectares: Double, val status: String)
data class Crop(val id: String, val nameEn: String, val category: String)
data class Animal(val id: String, val tagId: String, val species: String, val status: String)
data class Listing(val id: String, val quantityKg: Double, val pricePerKg: Double)
data class FinanceRecord(val id: String, val amount: Double, val category: String, val date: String)
data class WeatherData(val temperature: Double, val humidity: Int, val condition: String)
data class DashboardData(
    val alerts: List<Alert>,
    val metrics: Metrics,
    val aiInsights: List<AIInsight>
)
data class Alert(val type: String, val message: String, val priority: String)
data class Metrics(val activeCrops: Int, val livestockCount: Int, val monthlyProfit: Double)
data class AIInsight(val category: String, val message: String, val confidence: Double)
