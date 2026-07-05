package com.agrihub.platform.platform.featureflags

import java.util.UUID

class FeatureFlagService {
    
    data class FeatureFlag(
        val key: String,
        val description: String,
        val enabled: Boolean,
        val rolloutPercentage: Int,
        val enabledCountries: List<String>,
        val enabledTenants: List<UUID>
    )
    
    private val flags = mutableMapOf(
        "ai_assistant" to FeatureFlag("ai_assistant", "AI Chat and recommendations", true, 100, listOf("MW", "ZM", "TZ"), emptyList()),
        "marketplace" to FeatureFlag("marketplace", "Buy and sell produce", true, 100, listOf("MW", "ZM", "TZ"), emptyList()),
        "insurance" to FeatureFlag("insurance", "Crop and livestock insurance", false, 0, emptyList(), emptyList()),
        "loans" to FeatureFlag("loans", "Agricultural loan management", true, 100, listOf("MW", "ZM"), emptyList()),
        "weather" to FeatureFlag("weather", "Weather forecasts and alerts", true, 100, listOf("MW", "ZM", "TZ"), emptyList()),
        "offline_sync" to FeatureFlag("offline_sync", "Offline data synchronization", true, 100, listOf("MW", "ZM", "TZ"), emptyList()),
        "satellite_imagery" to FeatureFlag("satellite_imagery", "Satellite farm monitoring", false, 0, emptyList(), emptyList()),
        "iot_integration" to FeatureFlag("iot_integration", "IoT sensor integration", false, 0, emptyList(), emptyList())
    )
    
    fun isEnabled(key: String, countryCode: String, tenantId: UUID): Boolean {
        val flag = flags[key] ?: return false
        if (!flag.enabled) return false
        if (flag.enabledCountries.isNotEmpty() && countryCode !in flag.enabledCountries) return false
        if (flag.enabledTenants.isNotEmpty() && tenantId !in flag.enabledTenants) return false
        return true
    }
    
    fun getAllFlags(countryCode: String, tenantId: UUID): Map<String, Boolean> {
        return flags.keys.associateWith { isEnabled(it, countryCode, tenantId) }
    }
    
    fun setFlag(key: String, enabled: Boolean) {
        flags[key]?.let {
            flags[key] = it.copy(enabled = enabled)
        }
    }
}
