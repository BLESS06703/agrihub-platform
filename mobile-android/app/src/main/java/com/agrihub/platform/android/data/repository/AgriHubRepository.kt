package com.agrihub.platform.android.data.repository

import com.agrihub.platform.android.domain.model.*

class AgriHubRepository {
    suspend fun getDashboard(): DashboardData = DashboardData(
        alerts = listOf(
            Alert("WEATHER", "Rain expected tomorrow. Delay fertilizer application.", "HIGH"),
            Alert("CROP", "Field 3 maize is ready for top dressing.", "MEDIUM"),
            Alert("LIVESTOCK", "Three cattle vaccinations due this week.", "HIGH")
        ),
        metrics = Metrics(activeCrops = 5, livestockCount = 23, monthlyProfit = 2800000.0),
        aiInsights = listOf(
            AIInsight("DISEASE", "Two tomato plants show signs of early blight.", 0.89),
            AIInsight("YIELD", "Estimated monthly profit: MWK 2.8M based on current prices.", 0.92)
        )
    )
    
    suspend fun getFarms(): List<Farm> = listOf(
        Farm("1", "Mzuzu Coffee Farm", 12.5, "ACTIVE"),
        Farm("2", "Lilongwe Vegetable Farm", 8.2, "ACTIVE"),
        Farm("3", "Blantyre Mixed Farm", 25.0, "ACTIVE")
    )
    
    suspend fun getCropCatalog(): List<Crop> = listOf(
        Crop("1", "Maize", "CEREAL"),
        Crop("2", "Soybeans", "LEGUME"),
        Crop("3", "Tomato", "VEGETABLE")
    )
    
    suspend fun getAnimals(): List<Animal> = listOf(
        Animal("1", "COW-042", "CATTLE", "ALIVE"),
        Animal("2", "PIG-015", "PIG", "ALIVE"),
        Animal("3", "CHK-230", "CHICKEN", "ALIVE")
    )
    
    suspend fun getListings(): List<Listing> = listOf(
        Listing("1", 500.0, 800.0),
        Listing("2", 1200.0, 1200.0),
        Listing("3", 300.0, 1500.0)
    )
}
