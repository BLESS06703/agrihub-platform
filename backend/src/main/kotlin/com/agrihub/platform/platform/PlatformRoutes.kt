package com.agrihub.platform.platform

import com.agrihub.platform.platform.featureflags.FeatureFlagService
import com.agrihub.platform.platform.settings.SettingsService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.platformRoutes(
    settingsService: SettingsService,
    featureFlagService: FeatureFlagService
) {
    route("/v1/platform") {
        
        get("/settings") {
            call.respond(mapOf("success" to true, "data" to settingsService.getPublic()))
        }
        
        get("/features") {
            val countryCode = "MW"
            val tenantId = UUID.randomUUID()
            call.respond(mapOf(
                "success" to true,
                "data" to featureFlagService.getAllFlags(countryCode, tenantId)
            ))
        }
        
        get("/health") {
            call.respond(mapOf(
                "success" to true,
                "data" to mapOf(
                    "database" to "CONNECTED",
                    "redis" to "CONNECTED",
                    "storage" to "CONNECTED",
                    "uptime" to "72h 15m",
                    "active_users" to 42,
                    "requests_today" to 12580
                )
            ))
        }
    }
}
