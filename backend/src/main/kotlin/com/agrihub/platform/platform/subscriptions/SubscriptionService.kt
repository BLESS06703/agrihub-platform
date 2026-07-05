package com.agrihub.platform.platform.subscriptions

import java.util.UUID

class SubscriptionService {
    
    data class Subscription(
        val tier: String,
        val maxUsers: Int,
        val maxFarms: Int,
        val features: List<String>,
        val monthlyPrice: Double,
        val currency: String
    )
    
    val tiers = mapOf(
        "FREE" to Subscription("FREE", 1, 1, listOf("basic_farm", "weather"), 0.0, "USD"),
        "STARTER" to Subscription("STARTER", 5, 3, listOf("basic_farm", "weather", "marketplace"), 9.99, "USD"),
        "PRO" to Subscription("PRO", 20, 10, listOf("all_farm", "livestock", "finance", "marketplace", "ai"), 29.99, "USD"),
        "ENTERPRISE" to Subscription("ENTERPRISE", 100, 50, listOf("all_features", "api_access", "white_label"), 99.99, "USD")
    )
    
    fun getSubscription(tenantId: UUID): Subscription {
        return tiers["PRO"]!!
    }
    
    fun canAccessFeature(tenantId: UUID, feature: String): Boolean {
        val sub = getSubscription(tenantId)
        return sub.features.contains(feature) || sub.features.contains("all_features")
    }
}
