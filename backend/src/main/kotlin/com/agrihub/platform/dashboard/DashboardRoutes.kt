package com.agrihub.platform.dashboard

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.dashboardRoutes(dashboardService: DashboardService) {
    route("/v1/dashboard") {
        
        get {
            val tenantId = UUID.randomUUID() // From JWT
            val userId = UUID.randomUUID()   // From JWT
            
            val dashboard = dashboardService.getDashboard(tenantId, userId)
            call.respond(mapOf(
                "success" to true,
                "data" to dashboard,
                "timestamp" to java.time.Instant.now().toString()
            ))
        }
    }
}
