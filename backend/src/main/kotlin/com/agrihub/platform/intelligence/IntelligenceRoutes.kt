package com.agrihub.platform.intelligence

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.intelligenceRoutes(intelligenceService: IntelligenceService) {
    route("/v1/intelligence") {
        
        get("/report") {
            val tenantId = UUID.randomUUID()
            val report = intelligenceService.generateIntelligence(tenantId)
            call.respond(mapOf("success" to true, "data" to report))
        }
        
        get("/alerts") {
            val tenantId = UUID.randomUUID()
            val report = intelligenceService.generateIntelligence(tenantId)
            call.respond(mapOf("success" to true, "data" to report.alerts))
        }
        
        get("/forecasts") {
            val tenantId = UUID.randomUUID()
            val report = intelligenceService.generateIntelligence(tenantId)
            call.respond(mapOf("success" to true, "data" to report.forecasts))
        }
        
        get("/recommendations") {
            val tenantId = UUID.randomUUID()
            val report = intelligenceService.generateIntelligence(tenantId)
            call.respond(mapOf("success" to true, "data" to report.recommendations))
        }
        
        get("/risks") {
            val tenantId = UUID.randomUUID()
            val report = intelligenceService.generateIntelligence(tenantId)
            call.respond(mapOf("success" to true, "data" to report.risks))
        }
    }
}
